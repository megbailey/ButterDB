# ButterDB Query Language

ButterDB builds [Google Visualization API Query Language](https://developers.google.com/chart/interactive/docs/querylanguage) strings under the hood. Each query runs against a **single worksheet** (`gid`). Cross-sheet joins are not available; relationships load related sheets with follow-up queries.

## Builder → GViz mapping

| QueryBuilder | GViz clause |
|---|---|
| `select("name", "year")` | `select B,D` (column letters) |
| `where("status", "published")` | `where (B='published')` |
| `where("year", ">=", 2020)` | `where (D>=2020)` |
| `orWhere(...)` | `... or (...)` |
| `whereNull("deleted_at")` | `where (F is null)` |
| `whereNotNull(...)` | `where (F is not null)` |
| `whereIn("id", list)` | `where (A=1 or A=2 or ...)` |
| `whereNotIn(...)` | `where (A!=1 and A!=2 ...)` |
| `whereBetween("year", 2020, 2025)` | `where (D>=2020 and D<=2025)` |
| `where(q -> q.where(...).orWhere(...))` | parenthesized group |
| `orderBy("name")` / `orderByDesc("name")` | `order by B asc\|desc` |
| `groupBy("year")` | `group by D` |
| `limit(10)` / `offset(20)` | `limit 10 offset 20` |
| `sum` / `avg` / `min` / `max` | `select sum(D)` etc. |

## Supported operators

`=`, `!=`, `<`, `>`, `<=`, `>=`, `contains`, `starts with`, `ends with`, `matches`, `like`, `is null`, `is not null`

## Soft deletes

When a model uses `@SoftDeletes`, queries add `where (deleted_at is null)` unless `withTrashed()` or `onlyTrashed()` is set.

## Relationships (not GViz joins)

| Relation | Strategy |
|---|---|
| `hasOne` / `hasMany` | `Related.where(fk, parentPk)` |
| `belongsTo` | `Related.where(pk, child.fk)` |
| `belongsToMany` | pivot sheet → `whereIn` related keys |

Eager load with `with("posts", "roles")` to batch `whereIn` queries.

## Pivot tables vs GViz `pivot`

ButterDB “pivot” means an association join sheet (`role_user`). It is **not** the GViz `pivot` clause.

## Quotas

Prefer `limit`/`offset` and `chunk()` for large reads. Sheets API quotas favor read-heavy, low-write workloads.
