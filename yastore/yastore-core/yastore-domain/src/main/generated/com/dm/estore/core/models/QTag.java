package com.dm.estore.core.models;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QTag is a Querydsl query type for Tag
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QTag extends EntityPathBase<Tag> {

    private static final long serialVersionUID = 1432864299L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTag tag = new QTag("tag");

    public final QAbstractEntity _super;

    public final StringPath code = createString("code");

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

    public QTag(String variable) {
        this(Tag.class, forVariable(variable), INITS);
    }

    public QTag(Path<? extends Tag> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QTag(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QTag(PathMetadata<?> metadata, PathInits inits) {
        this(Tag.class, metadata, inits);
    }

    public QTag(Class<? extends Tag> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new QAbstractEntity(type, metadata, inits);
        this.createdBy = _super.createdBy;
        this.createdDate = _super.createdDate;
        this.id = _super.id;
        this.lastModifiedBy = _super.lastModifiedBy;
        this.lastModifiedDate = _super.lastModifiedDate;
    }

}

