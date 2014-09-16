package com.dm.estore.core.models;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QShoppingCart is a Querydsl query type for ShoppingCart
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QShoppingCart extends EntityPathBase<ShoppingCart> {

    private static final long serialVersionUID = 361412919L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QShoppingCart shoppingCart = new QShoppingCart("shoppingCart");

    public final QOrder _super;

    // inherited
    public final QAddress billingAddress;

    //inherited
    public final StringPath code;

    // inherited
    public final QUser createdBy;

    //inherited
    public final DateTimePath<org.joda.time.DateTime> createdDate;

    // inherited
    public final QUser customer;

    //inherited
    public final NumberPath<Long> id;

    // inherited
    public final QUser lastModifiedBy;

    //inherited
    public final DateTimePath<org.joda.time.DateTime> lastModifiedDate;

    //inherited
    public final SetPath<LineItem, QLineItem> lineItems;

    // inherited
    public final QAddress shippingAddress;

    public QShoppingCart(String variable) {
        this(ShoppingCart.class, forVariable(variable), INITS);
    }

    public QShoppingCart(Path<? extends ShoppingCart> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QShoppingCart(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QShoppingCart(PathMetadata<?> metadata, PathInits inits) {
        this(ShoppingCart.class, metadata, inits);
    }

    public QShoppingCart(Class<? extends ShoppingCart> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new QOrder(type, metadata, inits);
        this.billingAddress = _super.billingAddress;
        this.code = _super.code;
        this.createdBy = _super.createdBy;
        this.createdDate = _super.createdDate;
        this.customer = _super.customer;
        this.id = _super.id;
        this.lastModifiedBy = _super.lastModifiedBy;
        this.lastModifiedDate = _super.lastModifiedDate;
        this.lineItems = _super.lineItems;
        this.shippingAddress = _super.shippingAddress;
    }

}

