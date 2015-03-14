package ru.avnakidkin.competentum.shop.web;

import com.vaadin.data.Property;
import com.vaadin.data.Validator;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.UserError;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.Reindeer;
import ru.avnakidkin.competentum.shop.util.ViewController;

/**
 * Panel with settings - tills, steps, probabilities
 *
 * @author Alexey Nakidkin {@literal <avnakidkin@gmail.com>}
 */
class SettingPage extends Panel implements Button.ClickListener, Property.ValueChangeListener {

    public static final String SETUP_CMD = "setup settings";

    private static final int MAX_TILLS = 100;
    private static final int MAX_STEPS = 100;

    private final TextField tills, steps;
    private final CountedSlider childProbability, womanProbability, manProbability;
    private final Button confirmButton;

    private final transient ViewController viewController;

    public SettingPage(ViewController viewController) {
        this.viewController = viewController;

        final VerticalLayout content = new VerticalLayout();
        setContent(content);

        {// first row
            HorizontalLayout tfLayout = new HorizontalLayout();
            tfLayout.setSpacing(true);

            tills = new TextField("Кол-во касс");
            tills.setDescription("1..." + MAX_TILLS);
            tills.setImmediate(true);
            tills.setNullRepresentation("0");
            tills.setConverter(Integer.class);
            tills.setConversionError(tills.getDescription());
            tills.setRequired(true);
            tills.setRequiredError(tills.getDescription());
            tills.addValidator(new IntegerRangeValidator(tills.getDescription(), 1, MAX_TILLS));
            tills.setWidth(6, Unit.EM);
            tfLayout.addComponent(tills);

            steps = new TextField("Кол-во шагов");
            steps.setDescription("1..." + MAX_STEPS);
            steps.setImmediate(true);
            steps.setNullRepresentation("0");
            steps.setConverter(Integer.class);
            steps.setConversionError(steps.getDescription());
            steps.setRequired(true);
            steps.setRequiredError(steps.getDescription());
            steps.addValidator(new IntegerRangeValidator(steps.getDescription(), 1, MAX_STEPS));
            steps.setWidth(6, Unit.EM);
            tfLayout.addComponent(steps);

            content.addComponent(tfLayout);
        }

        {// probabilities
            childProbability = new CountedSlider("Дети", 0, 100);
            childProbability.setWidth(50, Unit.PERCENTAGE);
            content.addComponent(childProbability);

            womanProbability = new CountedSlider("Женщины", 0, 100);
            womanProbability.setWidth(50, Unit.PERCENTAGE);
            content.addComponent(womanProbability);

            manProbability = new CountedSlider("Мужчины", 0, 100);
            manProbability.setWidth(50, Unit.PERCENTAGE);
            content.addComponent(manProbability);

            confirmButton = new Button("OK", this);
            confirmButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);
            confirmButton.addStyleName(Reindeer.BUTTON_DEFAULT);
            confirmButton.setWidth(5, Unit.EM);
            content.addComponent(confirmButton);
        }

        {// default settings
            tills.setConvertedValue(3);
            steps.setConvertedValue(100);
            childProbability.setValue(10);
            womanProbability.setValue(40);
            manProbability.setValue(50);
        }

        content.setSpacing(true);
        content.setMargin(true);
        setSizeFull();
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        if (event.getButton() == confirmButton) {
            try {
                tills.validate();
                steps.validate();
            } catch (Validator.InvalidValueException e) {
                // exclude fields error on confirmButton
                return;
            }
            int tillsValue = (int) tills.getConvertedValue(),
                    stepsValue = (int) steps.getConvertedValue(),
                    childProbabilityValue = childProbability.getValue(),
                    womanProbabilityValue = womanProbability.getValue(),
                    manProbabilityValue = manProbability.getValue();
            if (childProbabilityValue > 0 || womanProbabilityValue > 0 || manProbabilityValue > 0) {
                viewController.processViewCommand(SettingPage.class, SETUP_CMD,
                        new int[]{tillsValue, stepsValue,
                                childProbabilityValue, womanProbabilityValue, manProbabilityValue});
            } else {
                confirmButton.setComponentError(new UserError("Магазин без покупателей?"));
            }
        }
    }

    @Override
    public void valueChange(Property.ValueChangeEvent event) {
        int children = childProbability.getValue(),
                women = womanProbability.getValue(),
                men = manProbability.getValue(),
                sum = children + women + men;

        childProbability.setLabel(children + "/" + sum);
        womanProbability.setLabel(women + "/" + sum);
        manProbability.setLabel(men + "/" + sum);
    }

    private class CountedSlider extends HorizontalLayout {

        private final Slider slider;
        private final Label label;

        public CountedSlider(String caption, int min, int max) {
            slider = new Slider(caption, min, max);
            slider.setDescription(min + "..." + max);
            slider.addValueChangeListener(SettingPage.this);
            slider.setWidth(100, Unit.PERCENTAGE);
            addComponent(slider);
            setExpandRatio(slider, 1);

            label = new Label(min + "/" + max);
            label.setHeight(2, Unit.EM);
            label.setWidth(7, Unit.EM);
            addComponent(label);
            setComponentAlignment(label, Alignment.BOTTOM_LEFT);

            setSpacing(true);
        }

        public int getValue() {
            return slider.getValue().intValue();
        }

        public void setValue(int value) {
            slider.setValue((double) value);
        }

        public void setLabel(String text) {
            label.setValue(text);
        }
    }
}