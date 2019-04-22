# agh-mwo-enroller-rest
meetings/ endpoint
demo: https://enroller-app.herokuapp.com/

| URL | Request |result |
| :-------------|:-------------:|:-------------|
| /meetings     | GET | list of all meetings |
| /meetings/{id}| GET | get meeting with given id| 
| /meetings | POST | add meeting|
| /meetings/{id}|DELETE|delete meeting with given id|
| /meetings/{id} | PUT | update meeting with given id|
|/meetings/{meetingId}/participants/{participantId}|POST| add participant to a meeting|
|/meetings/{meetingId}/participants/{participantId}|DELETE| remove participant from meeting |
|/meetings/{id}/participants | GET| returns participants for meeting with given id|
|/meetings/sorted/title |GET| return sorted meetings by titles|
|/meetings/search/?id=|GET| return participant's meetings |
|/meetings/search?title=...&description=... | GET | returns meetings with matching title and/or description |
