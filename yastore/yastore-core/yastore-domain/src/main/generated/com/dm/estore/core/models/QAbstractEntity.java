package com.dm.estore.core.models;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QAbstractEntity is a Querydsl query type for AbstractEntity
 */
@Generated("com.mysema.query.codegen.SupertypeSerializer")
public class QAbstractEntity extends EntityPathBase<AbstractEntity> {

    private static final long serialVersionUID = 1527087988L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAbstractEntity abstractEntity = new QAbstractEntity("abstractEntity");

    public final QAuditableEntity _super;

    // inherited
    public final QUser createdBy;

    //inherited
    public final DateTimePath<org.joda.time.DateTime> createdDate;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    // inherited
    public final QUser lastModifiedBy;

    //inherited
    public final DateTimePath<org.joda.time.DateTime> lastModifiedDate;

    public QAbstractEntity(String variable) {
        this(AbstractEntity.class, forVariable(variable), INITS);
    }

    public QAbstractEntity(Path<? extends AbstractEntity> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QAbstractEntity(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QAbstractEntity(PathMetadata<?> metadata, PathInits inits) {
        this(AbstractEntity.class, metadata, inits);
    }

    public QAbstractEntity(Class<? extends AbstractEntity> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new QAuditableEntity(type, metadata, inits);
        this.createdBy = _super.createdBy;
        this.createdDate = _super.createdDate;
        this.lastModifiedBy = _super.lastModifiedBy;
        this.lastModifiedDate = _super.lastModifiedDate;
    }

}

