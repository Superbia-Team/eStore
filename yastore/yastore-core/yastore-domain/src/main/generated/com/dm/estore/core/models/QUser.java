package com.dm.estore.core.models;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QUser is a Querydsl query type for User
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = 1469167450L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUser user = new QUser("user");

    public final QAbstractEntity _super;

    public final MapPath<String, String, StringPath> attributes = this.<String, String, StringPath>createMap("attributes", String.class, String.class, StringPath.class);

    // inherited
    public final QUser createdBy;

    //inherited
    public final DateTimePath<org.joda.time.DateTime> createdDate;

    public final StringPath dtype = createString("dtype");

    public final QEmailAddress emailAddress;

    public final StringPath firstName = createString("firstName");

    //inherited
    public final NumberPath<Long> id;

    // inherited
    public final QUser lastModifiedBy;

    //inherited
    public final DateTimePath<org.joda.time.DateTime> lastModifiedDate;

    public final StringPath lastName = createString("lastName");

    public final StringPath middleName = createString("middleName");

    public final StringPath userName = createString("userName");

    public QUser(String variable) {
        this(User.class, forVariable(variable), INITS);
    }

    public QUser(Path<? extends User> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QUser(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QUser(PathMetadata<?> metadata, PathInits inits) {
        this(User.class, metadata, inits);
    }

    public QUser(Class<? extends User> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new QAbstractEntity(type, metadata, inits);
        this.createdBy = _super.createdBy;
        this.createdDate = _super.createdDate;
        this.emailAddress = inits.isInitialized("emailAddress") ? new QEmailAddress(forProperty("emailAddress")) : null;
        this.id = _super.id;
        this.lastModifiedBy = _super.lastModifiedBy;
        this.lastModifiedDate = _super.lastModifiedDate;
    }

}

