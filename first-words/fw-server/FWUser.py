from google.appengine.ext import ndb

class FWUser(ndb.Model):
    username = ndb.StringProperty()
    password = ndb.StringProperty()
    lastLesson = ndb.StringProperty()
    approvedLessons = ndb.JsonProperty()
