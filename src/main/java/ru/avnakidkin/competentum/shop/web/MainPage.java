package ru.avnakidkin.competentum.shop.web;

import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import ru.avnakidkin.competentum.shop.domain.Till;
import ru.avnakidkin.competentum.shop.service.ShopService;
import ru.avnakidkin.competentum.shop.util.ViewController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Main view - shop
 *
 * @author Alexey Nakidkin {@literal <avnakidkin@gmail.com>}
 */
class MainPage extends VerticalLayout implements Button.ClickListener {

    /**
     * Command for viewController to open settings dialog
     */
    public static final String OPEN_SETTINGS_CMD = "open settings";
    /**
     * Steps for jumping
     */
    private static final int JUMP_SIZE = 10;

    private final transient ShopService shopService;
    private final transient ViewController viewController;
    private final transient ScheduledExecutorService scheduledExecutorService;

    private transient ScheduledFuture scheduledFuture;

    private final Button nextButton, skipButton, lastButton, autoButton, rebootButton;
    private final Label currentStepLabel;
    private final List<QueueLayout> queue;
    private final GridLayout out;

    private final int maxStep;
    private final List<Till> tills;

    private int currentStep = 0;

    public MainPage(int tillsSize, int maxStep, ShopService shopService, ViewController viewController) {
        this.maxStep = maxStep;
        this.shopService = shopService;
        this.viewController = viewController;
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        queue = new ArrayList<>(tillsSize);
        tills = new ArrayList<>(tillsSize);

        out = new GridLayout(tillsSize + 1, 3);
        out.setMargin(true);
        out.setSpacing(true);

        {// first column
            ThemeResource doorsResource = new ThemeResource("images/doors.png");
            Image doorsImg = new Image();
            doorsImg.setSource(doorsResource);
            out.addComponent(doorsImg, 0, 0);

            ThemeResource clockResource = new ThemeResource("images/clock.png");
            Image clockImg = new Image();
            clockImg.setSource(clockResource);
            out.addComponent(clockImg, 0, 2);
        }

        ThemeResource perfResource = new ThemeResource("images/till.png");
        for (int i = 0; i < tillsSize; i++) {
            Till till = shopService.createTill();
            tills.add(till);

            // till - second row
            ImageAndLabel perfLayout = new ImageAndLabel(perfResource, "" + till.getPerformance());
            out.addComponent(perfLayout, i + 1, 1);
            perfLayout.setMargin(new MarginInfo(false, true, false, true));

            // queue - third row
            QueueLayout queueLayout = new QueueLayout(till);
            queue.add(queueLayout);
            out.addComponent(queueLayout, i + 1, 2);
        }
        // panel for scrolling
        Panel shopPanel = new Panel(out);
        addComponent(shopPanel);
        shopPanel.setSizeFull();
        setExpandRatio(shopPanel, 1);

        {// buttons
            HorizontalLayout buttonsLayout = new HorizontalLayout();
            buttonsLayout.setWidth(100, Unit.PERCENTAGE);
            buttonsLayout.setSpacing(true);

            currentStepLabel = new Label("Step 0");
            buttonsLayout.addComponent(currentStepLabel);

            nextButton = new Button(">", this);
            nextButton.setDescription("+1 step");
            buttonsLayout.addComponent(nextButton);

            skipButton = new Button(">>", this);
            skipButton.setDescription("+" + JUMP_SIZE + " steps");
            buttonsLayout.addComponent(skipButton);

            lastButton = new Button(">>>", this);
            lastButton.setDescription("to the end");
            buttonsLayout.addComponent(lastButton);

            autoButton = new Button("play", this);
            autoButton.setDescription("show movie");
            buttonsLayout.addComponent(autoButton);

            rebootButton = new Button("reboot", this);
            rebootButton.setDescription("return to settings");
            buttonsLayout.addComponent(rebootButton);

            addComponent(buttonsLayout);
        }

        setSizeFull();
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        if (event.getButton() == nextButton) {
            step();
        } else if (event.getButton() == skipButton) {
            for (int i = 0; i < JUMP_SIZE && currentStep < maxStep; i++) {
                step();
            }
        } else if (event.getButton() == lastButton) {
            while (currentStep < maxStep) {
                step();
            }
        } else if (event.getButton() == autoButton) {
            if (scheduledFuture != null) {
                // stop it
                scheduledFuture.cancel(false);
                scheduledFuture = null;
                UI.getCurrent().setPollInterval(-1);

                nextButton.setEnabled(true);
                skipButton.setEnabled(true);
                lastButton.setEnabled(true);

                autoButton.setCaption("play");
                autoButton.setDescription("show movie");
            } else {
                // start it
                scheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            VaadinSession.getCurrent().lock();
                            step();
                        } finally {
                            VaadinSession.getCurrent().unlock();
                        }

                    }
                }, 2, 2, TimeUnit.SECONDS);
                UI.getCurrent().setPollInterval(2000);

                nextButton.setEnabled(false);
                skipButton.setEnabled(false);
                lastButton.setEnabled(false);

                autoButton.setCaption("stop");
                autoButton.setDescription("stop movie");
            }
        } else if (event.getButton() == rebootButton) {
            stop();
            viewController.processViewCommand(MainPage.class, OPEN_SETTINGS_CMD, null);
        }
    }

    private void stop() {
        nextButton.setEnabled(false);
        skipButton.setEnabled(false);
        lastButton.setEnabled(false);
        autoButton.setEnabled(false);
        if (scheduledFuture != null) {
            scheduledFuture.cancel(false);
            scheduledFuture = null;
            UI.getCurrent().setPollInterval(-1);
        }
        scheduledExecutorService.shutdown();
    }

    private void step() {
        Till grownTill = shopService.createAndPutCustomer(tills);
        for (QueueLayout q : queue) {
            q.updateCustomers(q.isYour(grownTill));
        }

        shopService.serviceCustomers(tills);
        for (int i = 1; i < out.getColumns(); i++) {
            out.removeComponent(i, 0);
        }
        for (int i = 0; i < queue.size(); i++) {
            CustomerLayout happyCustomer = queue.get(i).serviceCustomer();
            if (happyCustomer != null) {
                happyCustomer.reset();
                out.addComponent(happyCustomer, i + 1, 0);
            }
        }
        currentStep++;
        currentStepLabel.setValue("Step " + currentStep);

        if (currentStep == maxStep) {
            stop();
        }
    }
}