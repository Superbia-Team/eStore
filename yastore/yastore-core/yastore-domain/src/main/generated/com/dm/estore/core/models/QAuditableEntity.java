package com.dm.estore.core.models;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QAuditableEntity is a Querydsl query type for AuditableEntity
 */
@Generated("com.mysema.query.codegen.SupertypeSerializer")
public class QAuditableEntity extends EntityPathBase<AuditableEntity> {

    private static final long serialVersionUID = 667809033L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAuditableEntity auditableEntity = new QAuditableEntity("auditableEntity");

    public final QUser createdBy;

    public final DateTimePath<org.joda.time.DateTime> createdDate = createDateTime("createdDate", org.joda.time.DateTime.class);

    public final QUser lastModifiedBy;

    public final DateTimePath<org.joda.time.DateTime> lastModifiedDate = createDateTime("lastModifiedDate", org.joda.time.DateTime.class);

    public QAuditableEntity(String variable) {
        this(AuditableEntity.class, forVariable(variable), INITS);
    }

    public QAuditableEntity(Path<? extends AuditableEntity> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QAuditableEntity(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QAuditableEntity(PathMetadata<?> metadata, PathInits inits) {
        this(AuditableEntity.class, metadata, inits);
    }

    public QAuditableEntity(Class<? extends AuditableEntity> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.createdBy = inits.isInitialized("createdBy") ? new QUser(forProperty("createdBy"), inits.get("createdBy")) : null;
        this.lastModifiedBy = inits.isInitialized("lastModifiedBy") ? new QUser(forProperty("lastModifiedBy"), inits.get("lastModifiedBy")) : null;
    }

}

