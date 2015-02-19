import webapp2

from FWUtils import *
from FWUser import FWUser
from FWLesson import FWLesson

from google.appengine.api import search

class CreateColorsLessonService(webapp2.RequestHandler):

    def post(self):
        print 'Create Lesson Service post called'
        jsonObj = getJsonObjFromFormData(self.request.body)
        username = jsonObj['username']
        
        fwUsers = FWUser.query(FWUser.username==username).fetch()
        
        lessonName='Colors'
        lessonDescription = "Basic colors"
        
        lessonWords = ["Red","Blue","Green","Yellow","Black","Brown","Orange","Pink","White","Gray","Purple"]

        lessonContents = {
            "Red":{"word":"Red","imageUrl":""},
            "Blue":{"word":"Blue","imageUrl":""},
            "Green":{"word":"Green","imageUrl":""},
            "Yellow":{"word":"Yellow","imageUrl":""},
            "Black":{"word":"Black","imageUrl":""},
            "Brown":{"word":"Brown","imageUrl":""},
            "Orange":{"word":"Orange","imageUrl":""},
            "Pink":{"word":"Pink","imageUrl":""},
            "White":{"word":"White","imageUrl":""},
            "Gray":{"word":"Gray","imageUrl":""},
            "Purple":{"word":"Purple","imageUrl":""}
        }
                
        if len(fwUsers) > 0:
            fwUser = fwUsers[0]
            fwLesson = FWLesson(parent=fwUser.key,lessonName=lessonName, 
                                lessonContents = lessonContents, shared=True,
                                lessonDescription = lessonDescription,active=True,
                                lessonType=LESSON_TYPE_COLORS
                                );
            fwLessonKey = fwLesson.put()
            print 'Lesson saved with key ' + str(fwLessonKey)
            
            #Create index
            fields = [
                search.TextField(name='lessonName', value=lessonName),
                search.TextField(name='lessonDesc', value=lessonDescription),
                search.TextField(name='lessonWords', value= ",".join(lessonWords))
            ]            
            
            d = search.Document(doc_id='doc-' + username + '-' + lessonName.replace(' ','-'), fields=fields)
            
            try:
                add_result = search.Index(name=INDEX_NAME).put(d)
                print 'added to index' + str(add_result)
            except search.Error:
                print 'exception occurred while building index'

            jsonResponse = '{"status":"OK"}'
            self.response.set_status(201)
        else:
            self.response.set_status(400)
            print 'Invalid user!'
            jsonResponse = '{"status":"FAILED","error":"INVALID_USER"}'
            
                
        
        self.response.headers['Content-Type'] = 'application/json'
        self.response.out.write(jsonResponse)
            
        