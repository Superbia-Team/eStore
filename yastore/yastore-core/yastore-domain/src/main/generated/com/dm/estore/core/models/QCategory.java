package com.dm.estore.core.models;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QCategory is a Querydsl query type for Category
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QCategory extends EntityPathBase<Category> {

    private static final long serialVersionUID = 1373579533L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCategory category = new QCategory("category");

    public final QAbstractEntity _super;

    public final MapPath<String, String, StringPath> attributes = this.<String, String, StringPath>createMap("attributes", String.class, String.class, StringPath.class);

    public final StringPath code = createString("code");

    // inherited
    public final QUser createdBy;

    //inherited
    public final DateTimePath<org.joda.time.DateTime> createdDate;

    public final NumberPath<Integer> displayOrder = createNumber("displayOrder", Integer.class);

    public final BooleanPath homeCategory = createBoolean("homeCategory");

    //inherited
    public final NumberPath<Long> id;

    public final StringPath image = createString("image");

    // inherited
    public final QUser lastModifiedBy;

    //inherited
    public final DateTimePath<org.joda.time.DateTime> lastModifiedDate;

    public final QI18NString name;

    public final QCategory parent;

    public final QI18NString summary;

    public final ListPath<Tag, QTag> tags = this.<Tag, QTag>createList("tags", Tag.class, QTag.class, PathInits.DIRECT2);

    public final StringPath url = createString("url");

    public QCategory(String variable) {
        this(Category.class, forVariable(variable), INITS);
    }

    public QCategory(Path<? extends Category> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QCategory(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QCategory(PathMetadata<?> metadata, PathInits inits) {
        this(Category.class, metadata, inits);
    }

    public QCategory(Class<? extends Category> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new QAbstractEntity(type, metadata, inits);
        this.createdBy = _super.createdBy;
        this.createdDate = _super.createdDate;
        this.id = _super.id;
        this.lastModifiedBy = _super.lastModifiedBy;
        this.lastModifiedDate = _super.lastModifiedDate;
        this.name = inits.isInitialized("name") ? new QI18NString(forProperty("name")) : null;
        this.parent = inits.isInitialized("parent") ? new QCategory(forProperty("parent"), inits.get("parent")) : null;
        this.summary = inits.isInitialized("summary") ? new QI18NString(forProperty("summary")) : null;
    }

}

