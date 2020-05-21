// Full spec-compliant TodoMVC with localStorage persistence
// and hash-based routing in ~150 lines.

// localStorage persistence
var STORAGE_KEY = 'todos-vuejs-2.0'

// visibility filters
var filters = {
  all: function (todos) {
    return todos
  },
  active: function (todos) {
    return todos.filter(function (todo) {
      return !todo.completed
    })
  },
  completed: function (todos) {
    return todos.filter(function (todo) {
      return todo.completed
    })
  }
}

// app Vue instance
var app = new Vue({
  // app initial state
  data: {
    todos: [],
    newTodo: '',
    editedTodo: null,
    visibility: 'all'
  },
  created: function() {
    this.fetchData();
  },

  // watch todos change for localStorage persistence
  watch: {
    todos: {
      handler: function (todos) {
        console.log("this.todos changed!")
      },
      deep: true
    }
  },

  // computed properties
  // https://vuejs.org/guide/computed.html
  computed: {
    filteredTodos: function () {
      return filters[this.visibility](this.todos)
    },
    remaining: function () {
      return filters.active(this.todos).length
    },
    allDone: {
      get: function () {
        return this.remaining === 0
      },
      set: function (value) {
        this.todos.forEach(function (todo) {
          todo.completed = value
        })
      }
    }
  },

  filters: {
    pluralize: function (n) {
      return n === 1 ? 'item' : 'items'
    }
  },

  // methods that implement data logic.
  // note there's no DOM manipulation here at all.
  methods: {
    addTodo: function () {
      var value = this.newTodo && this.newTodo.trim()
      if (!value) {
        return
      }
      var xhr = new XMLHttpRequest()
      var self = this
      xhr.open('POST', "/todos")
      xhr.setRequestHeader("Content-type", "application/json");
      xhr.onload = function () {
        self.todos.push(JSON.parse(xhr.responseText))
      }
      xhr.send(JSON.stringify({ title: value, completed: false }));
      this.newTodo = ''
    },

    removeTodo: function (todo) {
      var xhr = new XMLHttpRequest()
      var self = this
      xhr.open('DELETE', "/todos/" + todo.id)
      xhr.onload = function () {
        self.todos.splice(self.todos.indexOf(todo), 1)
      }
      xhr.send()
    },

    editTodo: function (todo) {
      this.beforeEditCache = todo.title
      this.editedTodo = todo
    },

    doneEdit: function (todo) {
      if (!this.editedTodo) {
        return
      }
      this.editedTodo = null

      if (!todo.title) {
        return this.removeTodo(todo)
      }

      var xhr = new XMLHttpRequest()
      var self = this
      xhr.open('PUT', "/todos/" + todo.id)
      xhr.setRequestHeader("Content-type", "application/json");
      xhr.onload = function () {
        todo.title = todo.title.trim()
      }
      xhr.send(JSON.stringify({ title: todo.title, completed: todo.completed }));
    },

    cancelEdit: function (todo) {
      this.editedTodo = null
      todo.title = this.beforeEditCache
    },

    completedTodo: function (todo) {
      var xhr = new XMLHttpRequest()
      var self = this
      xhr.open('PUT', "/todos/" + todo.id)
      xhr.setRequestHeader("Content-type", "application/json");
      xhr.onload = function () {
        //todo.title = todo.title.trim()
      }
      xhr.send(JSON.stringify({ title: todo.title, completed: todo.completed }));
    },
    removeCompleted: function () {
      this.todos = filters.active(this.todos)
    },

    fetchData: function(){
      var xhr = new XMLHttpRequest()
      var self = this
      xhr.open('GET', "/todos")
      xhr.onload = function () {
        self.todos = JSON.parse(xhr.responseText)
      }
      xhr.send()
    }
  },

  // a custom directive to wait for the DOM to be updated
  // before focusing on the input field.
  // https://vuejs.org/guide/custom-directive.html
  directives: {
    'todo-focus': function (el, binding) {
      if (binding.value) {
        el.focus()
      }
    }
  }
})

// handle routing
function onHashChange () {
  var visibility = window.location.hash.replace(/#\/?/, '')
  if (filters[visibility]) {
    app.visibility = visibility
  } else {
    window.location.hash = ''
    app.visibility = 'all'
  }
}

window.addEventListener('hashchange', onHashChange)
onHashChange()

// mount
app.$mount('.todoapp')