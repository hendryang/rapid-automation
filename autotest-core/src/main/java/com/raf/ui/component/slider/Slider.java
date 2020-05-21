package com.raf.ui.component.slider;

import com.raf.ui.component.PageAction;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.UnexpectedTagNameException;

import java.util.List;
import java.util.Objects;

/**
 * Represents a Slider component
 *
 * @author Hendry Ang
 * @version %I%
 * @since 1.0.0-RC8
 */
@Log4j2
public class Slider {
    //Selector information
    private static final String SLIDER = "slider";
    private static final String TEXT_NOW_ATTRIB = "aria-valuetext";
    private static final String VALUE_MIN_ATTRIB = "aria-valuemin";
    private static final String VALUE_MAX_ATTRIB = "aria-valuemax";
    private static final String VALUE_NOW_ATTRIB = "aria-valuenow";
    private static final String LOWER_SLIDER = ".samplelower";
    private static final String UPPER_SLIDER = ".sampleupper";
    private static final String TICK_MARKS = ".sampletick-ue-large";
    private static final int DEFAULT_PIXEL = 10;
    private PageAction pageAction;
    private WebElement lowerSlider;
    private WebElement upperSlider;

    public Slider(PageAction po, WebElement sliderElement) {
        String tagName = sliderElement.getTagName();
        if (Objects.isNull(tagName) || !SLIDER.equalsIgnoreCase(tagName)) {
            throw new UnexpectedTagNameException(SLIDER, tagName);
        }

        this.pageAction = po;
        lowerSlider = sliderElement.findElement(By.cssSelector(LOWER_SLIDER));
        try {
            upperSlider = sliderElement.findElement(By.cssSelector(UPPER_SLIDER));
        } catch (NoSuchElementException e) {
            log.atDebug().log("[RAF] Detected Non-Range slider component");
            upperSlider = null;
        }
        List<WebElement> tickMarksElement = sliderElement.findElements(By.cssSelector(TICK_MARKS));
        if (tickMarksElement.size() > 2) {
            //todo handle slider with tick marks.
            throw new IllegalArgumentException("[RAF] Sorry! Slider with tick marks is currently not supported yet");
        }
    }

    /**
     * Set from and to range for the slider. This only works for slider that have range (ie. contains both lower and upper handler)
     *
     * @param from value for lower range (left-hand side)
     * @param to   value for upper range (right-hand side)
     * @return itself for chaining purpose.
     */
    public Slider setRange(int from, int to) {
        if (Objects.isNull(upperSlider))
            throw new IllegalArgumentException("Unable to setRange(...) for non-range slider. Please use setValue(...)");
        if (from > to)
            throw new IllegalArgumentException("Unable to set range from " + from + " to " + to + ". From is higher than To.");

        setLeftHandSliderValue(from).setRightHandSliderValue(to);
        return this;
    }

    /**
     * Set value only for the left-hand side slider or non-range slider (single value slider)
     *
     * @param value value for lower range (left-hand side)
     * @return itself for chaining purpose.
     */
    public Slider setLeftHandSliderValue(int value) {
        setValue(lowerSlider, value);
        return this;
    }

    /**
     * Set value only for the right-hand side slider. This only works for range slider.
     *
     * @param value value for upper range (right-hand side)
     * @return itself for chaining purpose.
     * @throws IllegalArgumentException if used with non-range slider
     */
    public Slider setRightHandSliderValue(int value) {
        if (Objects.isNull(upperSlider))
            throw new IllegalArgumentException("Unable to setUpperValue(...) for non-range slider. Please use setValue(...)");
        setValue(upperSlider, value);
        return this;
    }

    /**
     * Set value for value slider (non-range). This is essentially calling {@link #setLeftHandSliderValue(int)}
     *
     * @param value value for lower range (left-hand side)
     * @return itself for chaining purpose.
     */
    public Slider setValue(int value) {
        return setLeftHandSliderValue(value);
    }

    /**
     * @return current selected value/text in the slider
     */
    public int getValue() {
        return getLeftHandSliderSelectedText();
    }

    /**
     * @return array of currently selected range in the slider. <br>[0] for left-hand value <br>[1] for right-hand value
     */
    public int[] getRange() {
        return new int[]{this.getLeftHandSliderSelectedText(), this.getRightHandSliderSelectedText()};
    }

    /**
     * @return current selected value/text of the left-hand slider
     */
    public int getLeftHandSliderSelectedText() {
        return getCurrentText(lowerSlider);
    }

    /**
     * @return current selected value/text of the right-hand slider
     */
    public int getRightHandSliderSelectedText() {
        return getCurrentText(upperSlider);
    }


    //Setting the value based on given slider element (either lower or upper)
    // 1. it'll drag and drop the element by 10 pixel (faster) until it reach/pass the requested value
    // 2. if it passed, it'll reverse the drag and drop by 1 pixel (slower) until it reach the requested value
    private void setValue(WebElement slider, int value) {
        int counter = 0;
        int gap = value - getCurrentText(slider);
        int xOffset = DEFAULT_PIXEL;
        boolean isOK = true;
        if (gap > 0) { //moving to right
            while (getCurrentText(slider) < value) {
                //todo mechanism to determine if slider never move after few attempts
                counter++;
                if (counter >= 200) {
                    isOK = false;
                    log.atError().log("[RAF] Unable to drag the slider to value {}. Terminated to prevent endless loop!", value, getCurrentText(slider));
                    break;
                }
                if (getCurrentValue(slider) >= getMaxValue(slider) && getCurrentText(slider) < value) {
                    isOK = false;
                    log.atError().log("[RAF] Unable to set slider value to {}. the value is higher then maximum allowed which is {}", value, getCurrentText(slider));
                    break;
                }
                pageAction.dragAndDropBy(slider, xOffset, 0);
            }
            while (getCurrentText(slider) != value && isOK) {
                if (getCurrentValue(slider) <= 0) {
                    log.atError().log("[RAF] Unable to set slider value to {}. slider has hit the lowest possible number which is {}.", value, getCurrentText(slider));
                    break;
                }
                pageAction.dragAndDropBy(slider, -1 * (xOffset / DEFAULT_PIXEL), 0);
            }
        } else { //moving to left
            xOffset = -DEFAULT_PIXEL;
            while (getCurrentText(slider) > value) {
                counter++;
                if (counter >= 200) {
                    isOK = false;
                    log.atError().log("[RAF] Unable to drag the slider to value {}. Terminated to prevent endless loop!", value, getCurrentText(slider));
                    break;
                }
                if (getCurrentValue(slider) <= getMinValue(slider) && getCurrentText(slider) > value) {
                    isOK = false;
                    log.atError().log("[RAF] Unable to set slider value to {}. the value has hit the lowest possible value which is {}", value, getCurrentText(slider));
                    break;
                }
                pageAction.dragAndDropBy(slider, xOffset, 0);
            }
            while (getCurrentText(slider) != value && isOK) {
                if (getCurrentValue(slider) >= getMaxValue(slider)) {
                    log.atError().log("[RAF] Unable to set slider value to {}. slider has hit the highest possible number which is {}.", value, getCurrentText(slider));
                    break;
                }
                pageAction.dragAndDropBy(slider, -1 * (xOffset / DEFAULT_PIXEL), 0);
            }
        }
    }

    private int getCurrentText(WebElement slider) {
        checkSlider(slider);
        return Integer.parseInt(pageAction.getAttribute(slider, TEXT_NOW_ATTRIB));
    }

    private double getMaxValue(WebElement slider) {
        checkSlider(slider);
        return Double.parseDouble(pageAction.getAttribute(slider, VALUE_MAX_ATTRIB));
    }

    private double getCurrentValue(WebElement slider) {
        checkSlider(slider);
        return Double.parseDouble(pageAction.getAttribute(slider, VALUE_NOW_ATTRIB));
    }

    private double getMinValue(WebElement slider) {
        checkSlider(slider);
        return Double.parseDouble(pageAction.getAttribute(slider, VALUE_MIN_ATTRIB));
    }

    private void checkSlider(WebElement slider) {
        if (Objects.isNull(slider))
            throw new IllegalArgumentException("[RAF] Unable to get data of Upper Slider for non-range slider. Please use get data from Lower Slider.");
    }
}
