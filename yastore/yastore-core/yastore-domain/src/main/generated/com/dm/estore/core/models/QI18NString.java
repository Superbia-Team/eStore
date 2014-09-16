package com.dm.estore.core.models;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QI18NString is a Querydsl query type for I18NString
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QI18NString extends EntityPathBase<I18NString> {

    private static final long serialVersionUID = 139228158L;

    public static final QI18NString i18NString = new QI18NString("i18NString");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final MapPath<java.util.Locale, I18NStringValue, QI18NStringValue> strings = this.<java.util.Locale, I18NStringValue, QI18NStringValue>createMap("strings", java.util.Locale.class, I18NStringValue.class, QI18NStringValue.class);

    public QI18NString(String variable) {
        super(I18NString.class, forVariable(variable));
    }

    public QI18NString(Path<? extends I18NString> path) {
        super(path.getType(), path.getMetadata());
    }

    public QI18NString(PathMetadata<?> metadata) {
        super(I18NString.class, metadata);
    }

}

