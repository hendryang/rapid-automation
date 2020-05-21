// server.js
const jsonServer = require('json-server')
const server = jsonServer.create()
const router = jsonServer.router('db.json')
const middlewares = jsonServer.defaults()
const swaggerUi = require('swagger-ui-express');
const swaggerDocument = require('./public/swagger.json');

server.use('/api-docs', swaggerUi.serve, swaggerUi.setup(swaggerDocument));

server.use(middlewares)
server.use(router)
server.listen(3000, () => {
  console.log('JSON Server is running at localhost:3000')
})