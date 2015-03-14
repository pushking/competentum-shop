package ru.avnakidkin.competentum.shop.web;

import com.vaadin.server.Resource;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;

/**
 * Horizontal layout with image and text
 *
 * @author Alexey Nakidkin {@literal <avnakidkin@gmail.com>}
 */
class ImageAndLabel extends HorizontalLayout {

    private final Label label;

    public ImageAndLabel(Resource imageResource, String text) {
        Image image = new Image();
        image.setSource(imageResource);
        addComponent(image);

        label = new Label(text);
        addComponent(label);

        setSpacing(true);
    }

    public void setText(String newText) {
        label.setValue(newText);
    }
}