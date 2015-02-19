from google.appengine.ext import ndb
from FWUtils import *
from google.appengine.api import search

class FWLesson(ndb.Model):
    lessonName = ndb.StringProperty()
    lessonDescription = ndb.StringProperty()
    lessonContents = ndb.JsonProperty()
    shared = ndb.BooleanProperty(default=False)
    active = ndb.BooleanProperty(default=False)
    createdDate = ndb.IntegerProperty(default=getEpochTime())
    lessonType = ndb.StringProperty(default=LESSON_TYPE_ALPHA)
    votes = ndb.IntegerProperty(default=0)
    
    def _post_put_hook(self, future):
        print 'updating index for lesson ' + self.lessonName
        if self.shared:
            #Create index
            lessonWords = []
            
            for value in self.lessonContents.itervalues():
                lessonWord = value['word']
                print 'Adding word ' + lessonWord
                lessonWords.append(lessonWord)

            fields = [
                  search.TextField(name='lessonName', value=self.lessonName),
                  search.TextField(name='lessonDesc', value=self.lessonDescription),
                  search.TextField(name='lessonWords', value= ",".join(lessonWords)),
                  search.NumberField(name='votes', value=self.votes)
            ]            

            username = self.key.parent().get().username
            d = search.Document(doc_id='doc-' + username + '-' + self.lessonName.replace(' ','-'), fields=fields)
            
            try:
                add_result = search.Index(name=INDEX_NAME).put(d)
                print 'added to index' + str(add_result)
            except search.Error:
                print 'exception occurred while building index'
        else:
            print 'lesson ' + self.lessonName + 'is not shared. Skipping adding to search index. '
