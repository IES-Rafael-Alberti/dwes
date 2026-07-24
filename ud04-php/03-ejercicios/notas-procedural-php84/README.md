# Notes: a secure procedural CRUD

This guided exercise is the bridge between isolated PHP scripts and layered GTask. The teacher implements the first write operation live; students complete the remaining increments without receiving the final solution.

## Quick start

```bash
composer install
composer test
php -S localhost:8080 -t public
```

Run one deliberately failing contract with `vendor/bin/phpunit --group checkpoint-1`. Replace the number as you advance.

## Incremental route

| Stage | Goal and live demo | Student task | Test/checkpoint | Observable criterion |
|---|---|---|---|---|
| 0 | Teacher traces `index.php → dispatch()` | Boot PHP and inspect SQLite | `composer test` | two starter tests pass |
| 1 | Teacher writes `create_note()` with `prepare()` | Implement list and detail | `checkpoint-1` | a second connection reads the note |
| 2 | Demo validation at boundaries | Complete edit and delete | `checkpoint-2` | missing IDs do not mutate data |
| 3 | Demo a forged POST | Require CSRF on every mutation | `checkpoint-3` | absent/wrong tokens return `400` |
| 4 | Demo browser refresh after POST | Add PRG and generic errors | `checkpoint-4` | success returns `303` + `Location` |
| 5 | Demo an XSS payload as data | Escape every rendered value and refactor helpers | full suite supplied by teacher | payload appears as text, not markup |

At each stage: make the selected checkpoint RED, implement the smallest GREEN change, then remove duplication without changing behaviour.

## Contract

- Routes: `GET /notes`, `GET /notes/{id}`, `POST /notes`, `POST /notes/{id}/edit`, `POST /notes/{id}/delete`.
- Title: required, at most 80 characters. Body: required, at most 1000.
- All SQL values use prepared statements. All mutations require CSRF; successful mutations finish with PRG.
- Unknown routes/resources return `404`; unsupported methods return `405`; internal failures expose only a generic `500`.
- Rendered user data is escaped for HTML.

Login, users, roles, uploads and layers are intentionally out of scope. Those contracts belong to GTask.
