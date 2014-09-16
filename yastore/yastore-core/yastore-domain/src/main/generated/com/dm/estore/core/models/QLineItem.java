package com.dm.estore.core.models;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QLineItem is a Querydsl query type for LineItem
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QLineItem extends EntityPathBase<LineItem> {

    private static final long serialVersionUID = -1783566026L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QLineItem lineItem = new QLineItem("lineItem");

    public final QAbstractEntity _super;

    // inherited
    public final QUser createdBy;

    //inherited
    public final DateTimePath<org.joda.time.DateTime> createdDate;

    //inherited
    public final NumberPath<Long> id;

    // inherited
    public final QUser lastModifiedBy;

    //inherited
    public final DateTimePath<org.joda.time.DateTime> lastModifiedDate;

    public final NumberPath<Integer> lineNumber = createNumber("lineNumber", Integer.class);

    public final NumberPath<java.math.BigDecimal> price = createNumber("price", java.math.BigDecimal.class);

    public final QProduct product;

    public final NumberPath<Integer> quantity = createNumber("quantity", Integer.class);

    public QLineItem(String variable) {
        this(LineItem.class, forVariable(variable), INITS);
    }

    public QLineItem(Path<? extends LineItem> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QLineItem(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QLineItem(PathMetadata<?> metadata, PathInits inits) {
        this(LineItem.class, metadata, inits);
    }

    public QLineItem(Class<? extends LineItem> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new QAbstractEntity(type, metadata, inits);
        this.createdBy = _super.createdBy;
        this.createdDate = _super.createdDate;
        this.id = _super.id;
        this.lastModifiedBy = _super.lastModifiedBy;
        this.lastModifiedDate = _super.lastModifiedDate;
        this.product = inits.isInitialized("product") ? new QProduct(forProperty("product"), inits.get("product")) : null;
    }

}

