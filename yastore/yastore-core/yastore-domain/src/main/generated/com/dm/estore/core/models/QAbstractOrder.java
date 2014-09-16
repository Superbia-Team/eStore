package com.dm.estore.core.models;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QAbstractOrder is a Querydsl query type for AbstractOrder
 */
@Generated("com.mysema.query.codegen.SupertypeSerializer")
public class QAbstractOrder extends EntityPathBase<AbstractOrder> {

    private static final long serialVersionUID = -2019610211L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAbstractOrder abstractOrder = new QAbstractOrder("abstractOrder");

    public final QAbstractEntity _super;

    public final QAddress billingAddress;

    public final StringPath code = createString("code");

    // inherited
    public final QUser createdBy;

    //inherited
    public final DateTimePath<org.joda.time.DateTime> createdDate;

    public final QUser customer;

    //inherited
    public final NumberPath<Long> id;

    // inherited
    public final QUser lastModifiedBy;

    //inherited
    public final DateTimePath<org.joda.time.DateTime> lastModifiedDate;

    public final SetPath<LineItem, QLineItem> lineItems = this.<LineItem, QLineItem>createSet("lineItems", LineItem.class, QLineItem.class, PathInits.DIRECT2);

    public final QAddress shippingAddress;

    public QAbstractOrder(String variable) {
        this(AbstractOrder.class, forVariable(variable), INITS);
    }

    public QAbstractOrder(Path<? extends AbstractOrder> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QAbstractOrder(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QAbstractOrder(PathMetadata<?> metadata, PathInits inits) {
        this(AbstractOrder.class, metadata, inits);
    }

    public QAbstractOrder(Class<? extends AbstractOrder> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new QAbstractEntity(type, metadata, inits);
        this.billingAddress = inits.isInitialized("billingAddress") ? new QAddress(forProperty("billingAddress"), inits.get("billingAddress")) : null;
        this.createdBy = _super.createdBy;
        this.createdDate = _super.createdDate;
        this.customer = inits.isInitialized("customer") ? new QUser(forProperty("customer"), inits.get("customer")) : null;
        this.id = _super.id;
        this.lastModifiedBy = _super.lastModifiedBy;
        this.lastModifiedDate = _super.lastModifiedDate;
        this.shippingAddress = inits.isInitialized("shippingAddress") ? new QAddress(forProperty("shippingAddress"), inits.get("shippingAddress")) : null;
    }

}

