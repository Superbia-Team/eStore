package com.dm.estore.core.models;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QI18NStringValue is a Querydsl query type for I18NStringValue
 */
@Generated("com.mysema.query.codegen.EmbeddableSerializer")
public class QI18NStringValue extends BeanPath<I18NStringValue> {

    private static final long serialVersionUID = 987495443L;

    public static final QI18NStringValue i18NStringValue = new QI18NStringValue("i18NStringValue");

    public final StringPath value = createString("value");

    public QI18NStringValue(String variable) {
        super(I18NStringValue.class, forVariable(variable));
    }

    public QI18NStringValue(Path<? extends I18NStringValue> path) {
        super(path.getType(), path.getMetadata());
    }

    public QI18NStringValue(PathMetadata<?> metadata) {
        super(I18NStringValue.class, metadata);
    }

}

