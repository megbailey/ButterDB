# ButterDB

ButterDB is an Eloquent-style ORM for Google Sheets. Each `Model` subclass maps to one worksheet in a shared spreadsheet. Writes use the [Google Sheets API](https://developers.google.com/sheets/api/reference/rest); filtered reads use the [Google Visualization API Query Language](https://developers.google.com/chart/interactive/docs/querylanguage).

## Quick start

```java
new ButterDBManager("application.properties");

SampleObjectModel row = new SampleObjectModel();
row.setFieldValue("name", "Ada");
row.save();

var published = new SampleObjectModel()
    .newQuery()
    .where("name", "Ada")
    .orderByDesc("id")
    .limit(10)
    .get();
```

## Defining models

```java
public class SampleObjectModel extends Model {
    public SampleObjectModel() {
        super("id", new String[]{"id", "name", "code", "year"}, true);
    }
}
```

Optional annotations: `@Table`, `@PrimaryKey`, `@Column`, `@Timestamps`, `@SoftDeletes`, `@Fillable`, `@Guarded`.

## Core API

| Method | Description |
|---|---|
| `save()` | Insert or update |
| `delete()` / `forceDelete()` / `restore()` | Soft or hard delete |
| `find(id)` / `findOrFail(id)` | Fetch by primary key |
| `where` / `orWhere` / `whereIn` / `whereNull` | GViz filters |
| `orderBy` / `limit` / `offset` / `select` | Ordering and paging |
| `get()` | Returns a `ModelCollection` |
| `first()` / `create` / `updateOrCreate` | Query terminals |

See [docs/butterdb-query.md](docs/butterdb-query.md) for the query → GViz map.

## Relationships

```java
public HasMany<Post> posts() { return hasMany(Post.class, "user_id"); }
public BelongsTo<User> user() { return belongsTo(User.class, "user_id"); }
public BelongsToMany<Role> roles() {
    return belongsToMany(Role.class, "role_user", "user_id", "role_id");
}
```

Eager load with `.with("posts")`. Many-to-many uses a pivot worksheet (`attach` / `detach` / `sync`).

## Setup

1. Create a Google Spreadsheet and copy its ID.
2. Create a GCP service account, enable Sheets API, place the JSON key as `client_secret.json` under `src/main/resources`.
3. Share the spreadsheet with the service account (Editor).
4. Set `google.spreadsheet_id` / `google.client_secret` in `application.properties`.
5. Call `new ButterDBManager("application.properties")` before using models.

## Limitations

Sheets quotas favor read-heavy, low-write apps. There are no cross-sheet SQL joins or ACID transactions—relationships load related sheets with follow-up queries.
