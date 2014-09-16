package com.dm.estore.core.models;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QProduct is a Querydsl query type for Product
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QProduct extends EntityPathBase<Product> {

    private static final long serialVersionUID = 2088510208L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProduct product = new QProduct("product");

    public final QAbstractEntity _super;

    public final BooleanPath active = createBoolean("active");

    public final MapPath<String, String, StringPath> attributes = this.<String, String, StringPath>createMap("attributes", String.class, String.class, StringPath.class);

    public final QProduct baseProduct;

    public final BooleanPath bestSeller = createBoolean("bestSeller");

    public final ListPath<Category, QCategory> categories = this.<Category, QCategory>createList("categories", Category.class, QCategory.class, PathInits.DIRECT2);

    public final StringPath country = createString("country");

    // inherited
    public final QUser createdBy;

    //inherited
    public final DateTimePath<org.joda.time.DateTime> createdDate;

    public final QI18NString description;

    //inherited
    public final NumberPath<Long> id;

    public final StringPath image = createString("image");

    public final ListPath<String, StringPath> images = this.<String, StringPath>createList("images", String.class, StringPath.class, PathInits.DIRECT2);

    public final NumberPath<Integer> inventory = createNumber("inventory", Integer.class);

    // inherited
    public final QUser lastModifiedBy;

    //inherited
    public final DateTimePath<org.joda.time.DateTime> lastModifiedDate;

    public final StringPath listingImage = createString("listingImage");

    public final ComparablePath<org.joda.money.Money> msrp = createComparable("msrp", org.joda.money.Money.class);

    public final QI18NString name;

    public final BooleanPath newProduct = createBoolean("newProduct");

    public final NumberPath<Integer> popularity = createNumber("popularity", Integer.class);

    public final ComparablePath<org.joda.money.Money> price = createComparable("price", org.joda.money.Money.class);

    public final BooleanPath shippable = createBoolean("shippable");

    public final StringPath sku = createString("sku");

    public final QI18NString summary;

    public final ListPath<Tag, QTag> tags = this.<Tag, QTag>createList("tags", Tag.class, QTag.class, PathInits.DIRECT2);

    public final StringPath taxCode = createString("taxCode");

    public final StringPath variantGroup = createString("variantGroup");

    public final StringPath variantSubGroup = createString("variantSubGroup");

    public final NumberPath<Integer> weight = createNumber("weight", Integer.class);

    public QProduct(String variable) {
        this(Product.class, forVariable(variable), INITS);
    }

    public QProduct(Path<? extends Product> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QProduct(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QProduct(PathMetadata<?> metadata, PathInits inits) {
        this(Product.class, metadata, inits);
    }

    public QProduct(Class<? extends Product> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new QAbstractEntity(type, metadata, inits);
        this.baseProduct = inits.isInitialized("baseProduct") ? new QProduct(forProperty("baseProduct"), inits.get("baseProduct")) : null;
        this.createdBy = _super.createdBy;
        this.createdDate = _super.createdDate;
        this.description = inits.isInitialized("description") ? new QI18NString(forProperty("description")) : null;
        this.id = _super.id;
        this.lastModifiedBy = _super.lastModifiedBy;
        this.lastModifiedDate = _super.lastModifiedDate;
        this.name = inits.isInitialized("name") ? new QI18NString(forProperty("name")) : null;
        this.summary = inits.isInitialized("summary") ? new QI18NString(forProperty("summary")) : null;
    }

}

