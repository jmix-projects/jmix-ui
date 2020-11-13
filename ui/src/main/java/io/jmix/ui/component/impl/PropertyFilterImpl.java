/*
 * Copyright 2020 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.jmix.ui.component.impl;

import com.google.common.base.Preconditions;
import io.jmix.core.common.event.Subscription;
import io.jmix.core.querycondition.Condition;
import io.jmix.core.querycondition.LogicalCondition;
import io.jmix.core.querycondition.PropertyCondition;
import io.jmix.ui.UiComponents;
import io.jmix.ui.component.*;
import io.jmix.ui.icon.Icons;
import io.jmix.ui.model.DataLoader;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class PropertyFilterImpl<V> extends CompositeComponent<HBoxLayout> implements PropertyFilter<V>,
        CompositeWithHtmlCaption, CompositeWithHtmlDescription {

    protected UiComponents uiComponents;

    protected Label<String> captionLabel;
    protected HasValue<V> valueComponent;

    protected DataLoader dataLoader;
    protected String caption;
    protected String captionWidth;
    protected String icon;
    protected CaptionPosition captionPosition = CaptionPosition.LEFT;
    protected PropertyCondition propertyCondition = new PropertyCondition();
    protected boolean autoApply;

    public PropertyFilterImpl() {
        addCreateListener(this::onCreate);
    }

    @Autowired
    public void setUiComponents(UiComponents uiComponents) {
        this.uiComponents = uiComponents;
    }

    protected void onCreate(CreateEvent createEvent) {
        root = createRootComponent();
        initRootComponent(root);
        updateCaptionLayout();
    }

    protected HBoxLayout createRootComponent() {
        return uiComponents.create(HBoxLayout.class);
    }

    protected void initRootComponent(HBoxLayout root) {
        root.setSpacing(true);
    }

    protected void updateCaptionLayout() {
        if (captionPosition == CaptionPosition.LEFT) {
            root.setCaption(null);
            root.setIcon(null);

            if (captionLabel == null) {
                captionLabel = createCaptionLabel();
            }
            initCaptionLabel(captionLabel);
            root.add(captionLabel, 0);
        } else {
            root.remove(captionLabel);
            captionLabel = null;
            root.setCaption(caption);
            root.setIcon(icon);
        }
    }

    protected Label<String> createCaptionLabel() {
        return uiComponents.create(Label.TYPE_DEFAULT);
    }

    protected void initCaptionLabel(Label<String> label) {
        label.setAlignment(Alignment.MIDDLE_LEFT);
        label.setValue(caption);
        label.setWidth(captionWidth);
        label.setIcon(icon);
    }

    @Override
    public DataLoader getDataLoader() {
        return dataLoader;
    }

    @Override
    public void setDataLoader(DataLoader dataLoader) {
        this.dataLoader = dataLoader;

        Condition rootCondition = dataLoader.getCondition();
        if (rootCondition == null) {
            rootCondition = new LogicalCondition(LogicalCondition.Type.AND);
            dataLoader.setCondition(rootCondition);
        }

        if (rootCondition instanceof LogicalCondition) {
            ((LogicalCondition) rootCondition).add(propertyCondition);
        }
    }

    @Override
    public String getProperty() {
        return propertyCondition.getProperty();
    }

    @Override
    public void setProperty(String property) {
        this.propertyCondition.setProperty(property);
    }

    @Override
    public String getOperation() {
        return propertyCondition.getOperation();
    }

    @Override
    public void setOperation(String operation) {
        this.propertyCondition.setOperation(operation);
    }

    @Override
    public String getParameterName() {
        return this.propertyCondition.getParameterName();
    }

    @Override
    public void setParameterName(String parameterName) {
        this.propertyCondition.setParameterName(parameterName);
    }

    @Override
    public CaptionPosition getCaptionPosition() {
        return captionPosition;
    }

    @Override
    public void setCaptionPosition(CaptionPosition captionPosition) {
        if (this.captionPosition != captionPosition) {
            this.captionPosition = captionPosition;
            updateCaptionLayout();
        }
    }

    @Override
    public PropertyCondition getPropertyCondition() {
        return propertyCondition;
    }

    @Override
    public void setPropertyCondition(PropertyCondition propertyCondition) {
        this.propertyCondition = propertyCondition;

        // TODO: gg, update loader's conditions
    }

    @Nullable
    @Override
    public V getValue() {
        checkValueComponentState();
        return valueComponent.getValue();
    }

    @Override
    public void setValue(@Nullable V value) {
        checkValueComponentState();
        valueComponent.setValue(value);
    }

    @Override
    public HasValue<V> getValueComponent() {
        return valueComponent;
    }

    @Override
    public void setValueComponent(HasValue<V> valueComponent) {
        if (this.valueComponent != null) {
            root.remove(valueComponent);
        }

        this.valueComponent = valueComponent;
        initValueComponent(valueComponent);

        root.add(valueComponent);
    }

    protected void initValueComponent(HasValue<V> valueComponent) {
        valueComponent.addValueChangeListener(this::onValueChanged);
    }

    protected void onValueChanged(ValueChangeEvent<V> valueChangeEvent) {
        V value = valueChangeEvent.getValue();
        propertyCondition.setParameterValue(value);

        if (autoApply) {
            dataLoader.load();
        }

        ValueChangeEvent<V> event = new ValueChangeEvent<>(this,
                valueChangeEvent.getPrevValue(),
                valueChangeEvent.getValue(),
                valueChangeEvent.isUserOriginated());
        publish(ValueChangeEvent.class, event);
    }

    @Override
    public Subscription addValueChangeListener(Consumer<ValueChangeEvent<V>> listener) {
        return getEventHub().subscribe(ValueChangeEvent.class, (Consumer) listener);
    }

    @Override
    public void setCaption(@Nullable String caption) {
        this.caption = caption;
        updateCaption();
    }

    protected void updateCaption() {
        if (captionPosition == CaptionPosition.TOP) {
            root.setCaption(caption);
        } else {
            captionLabel.setValue(caption);
        }
    }

    @Nullable
    @Override
    public String getCaption() {
        return caption;
    }

    @Override
    public float getCaptionWidth() {
        return captionLabel != null ? captionLabel.getWidth() : AUTO_SIZE_PX;
    }

    @Override
    public SizeUnit getCaptionWidthSizeUnit() {
        return captionLabel != null ? captionLabel.getWidthSizeUnit() : SizeUnit.PIXELS;
    }

    @Override
    public void setCaptionWidth(String captionWidth) {
        this.captionWidth = captionWidth;

        if (captionLabel != null) {
            captionLabel.setWidth(captionWidth);
        }
    }

    @Override
    public boolean isAutoApply() {
        return autoApply;
    }

    @Override
    public void setAutoApply(boolean autoApply) {
        this.autoApply = autoApply;
    }

    @Override
    public void setWidth(@Nullable String width) {
        super.setWidth(width);

        if (Component.AUTO_SIZE.equals(width) || width == null) {
            root.resetExpanded();
            valueComponent.setWidthAuto();
        } else {
            root.expand(valueComponent);
        }
    }

    @Nullable
    @Override
    public String getIcon() {
        return icon;
    }

    @Override
    public void setIcon(@Nullable String icon) {
        this.icon = icon;
        updateIcon();
    }

    protected void updateIcon() {
        if (captionPosition == CaptionPosition.TOP) {
            root.setIcon(icon);
        } else {
            captionLabel.setIcon(icon);
        }
    }

    @Override
    public void setIconFromSet(@Nullable Icons.Icon icon) {
        String iconName = getIconName(icon);
        setIcon(iconName);
    }

    @Nullable
    protected String getIconName(@Nullable Icons.Icon icon) {
        return applicationContext.getBean(Icons.class).get(icon);
    }

    @Override
    public boolean isEditable() {
        return valueComponent instanceof Editable
                && ((Editable) valueComponent).isEditable();
    }

    @Override
    public void setEditable(boolean editable) {
        if (valueComponent instanceof Editable) {
            ((Editable) valueComponent).setEditable(editable);
        }
    }

    @Override
    public void focus() {
        if (valueComponent instanceof Focusable) {
            ((Focusable) valueComponent).focus();
        }
    }

    @Override
    public int getTabIndex() {
        return valueComponent instanceof Focusable
                ? ((Focusable) valueComponent).getTabIndex()
                : 0;
    }

    @Override
    public void setTabIndex(int tabIndex) {
        if (valueComponent instanceof Focusable) {
            ((Focusable) valueComponent).setTabIndex(tabIndex);
        }
    }

    protected void checkValueComponentState() {
        Preconditions.checkState(valueComponent != null, "Value component isn't set");
    }
}
