# Eredivisie API

An API to fetch information about the dutch football league.
Straight from JSON files to keep it as simple as possible.

Available endpoints, but not limited to:
- GET teams/ - Fetch all the teams
- GET teams/{id} - Fetch a specific id.
- GET players/ - Fetch all players
- GET player/{id} - Fetch player by id.

- POST teams/ - Add a new team,
An example HTTP request body:
```json
{
    "name": "VV Aengwirden",
    "city": "Tjalleberd",
    "stadium": "Sportpark by de Fjilden",
    "surface": "kunstgras",
    "since": 1986
}
```

- POST player/ - Add a new player
An example HTTP request body:
```json
{
    "name": "Alex Bouma",
    "dob": "07.09.1993",
    "height": "179 cm",
    "position": "Defender",
    "quality": 50
}
```

- PUT teams/ - Overwrite a team.
An example HTTP request body:
```json
{
  "name" : "FC Knudde",
  "id" : 2,
  "city" : "Amsterdam",
  "since" : 1956,
  "surface" : "Hybride",
  "stadium" : "Johan Cruijff ArenA"
}
```

- PATCH teams/ - Update one or more fields of a team.
An example HTTP request body:
```json
{
  "id" : 3,
  "name" : "FC Knudde XL",
  "since" : 1977
}
```

- DELETE teams/ - Delete a team.
An example HTTP request body:
```json
{
  "id" : 18
}
```




