package org.vaadin.johannesd.vquery.demo;

import static org.vaadin.johannesd.vquery.VQuery.$;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Slider;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 * Main UI class
 */
@SuppressWarnings("serial")
public class VQueryDemoUI extends UI {
	
	@WebServlet("/*")
	@VaadinServletConfiguration(productionMode=false, ui = VQueryDemoUI.class)
	public static class DemoServlet extends VaadinServlet {
	}

    @Override
    protected void init(VaadinRequest request) {
        getPage().getStyles().add(
                ".my-style { background-color: #FBA !important; }");
        getPage().getStyles().add(
                ".my-style-2 { outline: 2px dotted #235 !important; }");

        GridLayout layout = new GridLayout(3, 1);
        layout.setSizeFull();
        layout.setSpacing(true);
        layout.setMargin(true);
        setContent(layout);

        layout.addComponent(new Button("Add my-style-2 to leaf components",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        $().isLeaf(true).addStyleName("my-style-2");

                    }
                }));
        layout.addComponent(new Button("Disable buttons 4..7",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        $().is(Button.class).slice(4, 8).setEnabled(false);
                    }
                }));
        layout.addComponent(new Button(
                "Add my-style to components that have labels and whose index is 2 or 3",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        $().is(Label.class).parent().slice(2, 4)
                                .addStyleName("my-style");
                    }
                }));

        for (int i = 0; i < 9; ++i) {
            layout.addComponent(test());
        }
    }

    private Component test() {
        VerticalLayout l = new VerticalLayout();
        l.setSpacing(true);
        l.setMargin(true);
        Panel p = new Panel(l);
        p.setSizeFull();

        for (int i = 0; i < 5; ++i) {
            Component c = null;
            int n = (int) (Math.random() * 4);
            switch (n) {
            case 0:
                c = new Button("bar");
                break;
            case 1:
                c = new TextField();
                break;
            case 2:
                c = new Slider();
                c.setWidth("100px");
                break;
            case 3:
                c = new Label("foo");
                break;
            case 4:
                break;
            case 5:
                break;
            }
            l.addComponent(c);
        }
        return p;
    }
}