/*
 * Copyright 2019 Haulmont.
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
package io.jmix.ui.xml.layout.loader;

import com.google.common.base.Strings;
import io.jmix.core.DevelopmentException;
import io.jmix.core.MetadataTools;
import io.jmix.core.metamodel.model.MetaClass;
import io.jmix.core.metamodel.model.MetaProperty;
import io.jmix.core.metamodel.model.MetaPropertyPath;
import io.jmix.ui.GuiDevelopmentException;
import io.jmix.ui.UiProperties;
import io.jmix.ui.component.*;
import io.jmix.ui.component.SupportsCaptionPosition.CaptionPosition;
import io.jmix.ui.component.factory.PropertyFilterComponentGenerationContext;
import io.jmix.ui.model.DataLoader;
import io.jmix.ui.model.ScreenData;
import io.jmix.ui.screen.FrameOwner;
import io.jmix.ui.screen.UiControllerUtils;
import io.jmix.ui.xml.layout.ComponentLoader;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;

import static io.jmix.core.querycondition.PropertyConditionUtils.generateParameterName;

public class PropertyFilterLoader extends AbstractComponentLoader<PropertyFilter> {

    public static final String OPERATION_BASE_MESSAGE_KEY = "io.jmix.core.querycondition/propertyfilter.";

    @Override
    public void createComponent() {
        resultComponent = factory.create(PropertyFilter.NAME);
        loadId(resultComponent, element);
    }

    @Override
    public void loadComponent() {
        assignFrame(resultComponent);

        loadVisible(resultComponent, element);
        loadWidth(resultComponent, element);
        loadHeight(resultComponent, element);

        loadEnable(resultComponent, element);

        loadStyleName(resultComponent, element);

        loadDescription(resultComponent, element);
        loadIcon(resultComponent, element);
        loadCss(resultComponent, element);

        loadString(element, "property", resultComponent::setProperty);
        loadString(element, "operation", resultComponent::setOperation);

        resultComponent.setParameterName(loadString(element, "parameterName")
                .orElse(generateParameterName(resultComponent.getPropertyCondition())));

        loadEnum(element, CaptionPosition.class, "captionPosition",
                resultComponent::setCaptionPosition);
        loadString(element, "captionWidth", resultComponent::setCaptionWidth);

        // TODO: gg, is it really can be thrown?
        if (Strings.isNullOrEmpty(resultComponent.getId()) &&
                Strings.isNullOrEmpty(resultComponent.getParameterName())) {
            throw new DevelopmentException("Either id or parameterName should be defined for propertyFilter");
        }

        loadDataLoader(resultComponent, element);
        loadValueComponent(resultComponent, element);

        loadCaption(resultComponent, element);

        resultComponent.setAutoApply(loadBoolean(element, "autoApply")
                .orElse(getUiProperties().isPropertyFilterAutoApply()));
    }

    protected void loadDataLoader(PropertyFilter<?> resultComponent, Element element) {
        String dataLoaderId = element.attributeValue("dataLoader");
        if (StringUtils.isNotBlank(dataLoaderId)) {
            FrameOwner frameOwner = getComponentContext().getFrame().getFrameOwner();
            ScreenData screenData = UiControllerUtils.getScreenData(frameOwner);
            DataLoader dataLoader = screenData.getLoader(dataLoaderId);

            resultComponent.setDataLoader(dataLoader);
        }
    }

    @Override
    protected void loadCaption(Component.HasCaption component, Element element) {
        super.loadCaption(component, element);

        if (component.getCaption() == null) {
            String caption = getDefaultCaption();
            component.setCaption(caption);
        }
    }

    /**
     * Default caption consist of the related entity property caption and the operation caption (if the operation
     * caption is configured to be visible), e.g. "Last name contains".
     */
    protected String getDefaultCaption() {
        MetaClass metaClass = resultComponent.getDataLoader().getContainer().getEntityMetaClass();
        MetaPropertyPath mpp = metaClass.getPropertyPath(resultComponent.getProperty());
        if (mpp == null) {
            return resultComponent.getProperty();
        } else {
            MetaProperty[] metaProperties = mpp.getMetaProperties();
            StringBuilder sb = new StringBuilder();

            MetaPropertyPath parentMpp = null;
            MetaClass tempMetaClass;

            for (int i = 0; i < metaProperties.length; i++) {
                if (i == 0) {
                    parentMpp = new MetaPropertyPath(metaClass, metaProperties[i]);
                    tempMetaClass = metaClass;
                } else {
                    parentMpp = new MetaPropertyPath(parentMpp, metaProperties[i]);
                    tempMetaClass = getMetadataTools().getPropertyEnclosingMetaClass(parentMpp);
                }

                sb.append(getMessageTools().getPropertyCaption(tempMetaClass, metaProperties[i].getName()));
                if (i < metaProperties.length - 1) {
                    sb.append(".");
                }
            }
            if (isOperationCaptionVisible()) {
                sb.append(" ").append(getOperationCaption(resultComponent.getOperation()));
            }
            return sb.toString();
        }
    }

    protected String getOperationCaption(String operation) {
        return getMessages().getMessage(OPERATION_BASE_MESSAGE_KEY + operation);
    }

    protected void loadValueComponent(PropertyFilter<?> resultComponent, Element element) {
        Component valueComponent;

        if (!element.elements().isEmpty()) {
            Element valueComponentElement = element.elements().get(0);

            ComponentLoader<?> valueComponentLoader = getLayoutLoader().createComponent(valueComponentElement);
            valueComponentLoader.loadComponent();
            valueComponent = valueComponentLoader.getResultComponent();
        } else {
            ComponentGenerationContext context = new PropertyFilterComponentGenerationContext(
                    resultComponent.getDataLoader().getContainer().getEntityMetaClass(),
                    resultComponent.getPropertyCondition());
            context.setTargetClass(PropertyFilter.class);

            valueComponent = getUiComponentsGenerator().generate(context);
        }

        if (!(valueComponent instanceof HasValue)) {
            throw new GuiDevelopmentException("Value component of the PropertyFilter must implement HasValue",
                    getComponentContext().getCurrentFrameId());
        }

        resultComponent.setValueComponent((HasValue) valueComponent);
    }

    protected UiComponentsGenerator getUiComponentsGenerator() {
        return (UiComponentsGenerator) applicationContext.getBean(UiComponentsGenerator.NAME);
    }

    protected MetadataTools getMetadataTools() {
        return applicationContext.getBean(MetadataTools.class);
    }

    protected UiProperties getUiProperties() {
        return applicationContext.getBean(UiProperties.class);
    }

    protected boolean isOperationCaptionVisible() {
        return loadBoolean(element, "operationCaptionVisible").orElse(true);
    }
}
