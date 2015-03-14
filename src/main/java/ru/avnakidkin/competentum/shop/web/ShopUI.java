package ru.avnakidkin.competentum.shop.web;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;
import ru.avnakidkin.competentum.shop.service.ShopService;
import ru.avnakidkin.competentum.shop.service.impl.ShopServiceImpl;
import ru.avnakidkin.competentum.shop.util.ViewController;

import javax.servlet.annotation.WebServlet;

/**
 * Main class
 *
 * @author Alexey Nakidkin {@literal <avnakidkin@gmail.com>}
 */
@Theme("competentumshop")
@Widgetset("ru.avnakidkin.competentum.shop.CompetentumShopWidgetset")
@PreserveOnRefresh
public class ShopUI extends UI implements ViewController {

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        setContent(new SettingPage(this));
    }

    @Override
    public void processViewCommand(Class viewClass, String command, Object data) {
        if (SettingPage.class == viewClass) {
            switch (command) {
                case SettingPage.SETUP_CMD: {
                    int[] params = (int[]) data;
                    ShopService shopService = new ShopServiceImpl(params[2], params[3], params[4]);
                    setContent(new MainPage(params[0], params[1], shopService, this));
                    break;
                }
                default:
                    throw new IllegalArgumentException(String.format("Unknown command for %s: %s", viewClass, command));
            }
        } else if (MainPage.class == viewClass) {
            switch (command) {
                case MainPage.OPEN_SETTINGS_CMD: {
                    setContent(new SettingPage(this));
                    break;
                }
                default:
                    throw new IllegalArgumentException(String.format("Unknown command for %s: %s", viewClass, command));
            }
        } else {
            throw new IllegalArgumentException(String.format("Unknown class: %s", viewClass));
        }
    }

    @WebServlet(urlPatterns = "/*", name = "CompetentumShopServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = ShopUI.class, productionMode = false)
    public static class CompetentumShopServlet extends VaadinServlet {
    }
}
