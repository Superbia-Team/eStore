package com.dm.estore.core.models;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QCustomer is a Querydsl query type for Customer
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QCustomer extends EntityPathBase<Customer> {

    private static final long serialVersionUID = 1929243629L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCustomer customer = new QCustomer("customer");

    public final QUser _super;

    public final SetPath<Address, QAddress> addresses = this.<Address, QAddress>createSet("addresses", Address.class, QAddress.class, PathInits.DIRECT2);

    //inherited
    public final MapPath<String, String, StringPath> attributes;

    public final DateTimePath<org.joda.time.DateTime> birthday = createDateTime("birthday", org.joda.time.DateTime.class);

    // inherited
    public final QUser createdBy;

    //inherited
    public final DateTimePath<org.joda.time.DateTime> createdDate;

    //inherited
    public final StringPath dtype;

    // inherited
    public final QEmailAddress emailAddress;

    //inherited
    public final StringPath firstName;

    //inherited
    public final NumberPath<Long> id;

    // inherited
    public final QUser lastModifiedBy;

    //inherited
    public final DateTimePath<org.joda.time.DateTime> lastModifiedDate;

    //inherited
    public final StringPath lastName;

    //inherited
    public final StringPath middleName;

    //inherited
    public final StringPath userName;

    public QCustomer(String variable) {
        this(Customer.class, forVariable(variable), INITS);
    }

    public QCustomer(Path<? extends Customer> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QCustomer(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QCustomer(PathMetadata<?> metadata, PathInits inits) {
        this(Customer.class, metadata, inits);
    }

    public QCustomer(Class<? extends Customer> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new QUser(type, metadata, inits);
        this.attributes = _super.attributes;
        this.createdBy = _super.createdBy;
        this.createdDate = _super.createdDate;
        this.dtype = _super.dtype;
        this.emailAddress = _super.emailAddress;
        this.firstName = _super.firstName;
        this.id = _super.id;
        this.lastModifiedBy = _super.lastModifiedBy;
        this.lastModifiedDate = _super.lastModifiedDate;
        this.lastName = _super.lastName;
        this.middleName = _super.middleName;
        this.userName = _super.userName;
    }

}

