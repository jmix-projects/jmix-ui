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

import io.jmix.core.common.event.Subscription;
import io.jmix.core.querycondition.PropertyCondition;
import io.jmix.ui.UiComponents;
import io.jmix.ui.component.*;
import io.jmix.ui.model.DataLoader;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class PropertyFilterImpl<V> extends CompositeComponent<HBoxLayout> implements PropertyFilter<V>,
        CompositeWithDescription {

    protected UiComponents uiComponents;

    protected Label<String> captionLabel;
    protected HasValue<V> valueComponent;

    protected DataLoader dataLoader;
    protected String caption;
    protected String captionWidth;
    protected CaptionPosition captionPosition = CaptionPosition.LEFT;
    protected PropertyCondition propertyCondition = new PropertyCondition();
    protected boolean autoApply;

    public PropertyFilterImpl() {
        addCreateListener(this::onCreate);
    }

    protected void onCreate(CreateEvent createEvent) {
        createComponents();
        updateLayout();
    }

    protected void createComponents() {
        root = createRootComponent();
        root.setSpacing(true);

        // TODO: gg, don't create a default field
        valueComponent = uiComponents.create(TextField.class);
        initValueComponent(valueComponent);
    }

    protected HBoxLayout createRootComponent() {
        return uiComponents.create(HBoxLayout.class);
    }

    protected Label<String> createCaptionLabel() {
        Label<String> captionLabel = uiComponents.create(Label.TYPE_DEFAULT);
        captionLabel.setAlignment(Alignment.MIDDLE_LEFT);

        return captionLabel;
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

    protected void updateLayout() {
        root.removeAll();

        if (captionPosition == CaptionPosition.LEFT) {
            root.setCaption(null);

            if (captionLabel == null) {
                captionLabel = createCaptionLabel();
            }

            root.add(captionLabel);
        } else {
            captionLabel = null;
            root.setCaption(caption);
        }

        root.add(valueComponent);
    }

    @Autowired
    public void setUiComponents(UiComponents uiComponents) {
        this.uiComponents = uiComponents;
    }

    @Override
    public DataLoader getDataLoader() {
        return dataLoader;
    }

    @Override
    public void setDataLoader(DataLoader dataLoader) {
        // TODO: gg, check if set
        this.dataLoader = dataLoader;
    }

    @Override
    public String getProperty() {
        return propertyCondition.getProperty();
    }

    @Override
    public void setProperty(String property) {
        // TODO: gg, check if set
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
        this.captionPosition = captionPosition;
        updateLayout();
    }

    @Override
    public PropertyCondition getPropertyCondition() {
        return propertyCondition;
    }

    @Override
    public void setPropertyCondition(PropertyCondition propertyCondition) {
        this.propertyCondition = propertyCondition;
    }

    @Nullable
    @Override
    public V getValue() {
        return valueComponent.getValue();
    }

    @Override
    public void setValue(@Nullable V value) {
        valueComponent.setValue(value);
    }

    @Override
    public HasValue<V> getValueComponent() {
        return valueComponent;
    }

    @Override
    public void setValueComponent(HasValue<V> valueComponent) {
        this.valueComponent = valueComponent;
        initValueComponent(valueComponent);
        updateLayout();
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
}
