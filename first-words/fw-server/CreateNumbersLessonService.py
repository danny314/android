import webapp2

from FWUtils import *
from FWUser import FWUser
from FWLesson import FWLesson

from google.appengine.api import search

class CreateNumbersLessonService(webapp2.RequestHandler):

    def post(self):
        print 'Create Lesson Service post called'
        jsonObj = getJsonObjFromFormData(self.request.body)
        username = jsonObj['username']
        
        fwUsers = FWUser.query(FWUser.username==username).fetch()
        
        lessonName='Numbers'
        lessonDescription = "Numbers 1 through 10"
        
        lessonWords = ["One","Two","Three","Four","Five","Six","Seven","Eight","Nine","Ten"]
        
        lessonContents = {
            "1":{"word":"One","imageUrl":""},
            "2":{"word":"Two","imageUrl":""},
            "3":{"word":"Three","imageUrl":""},
            "4":{"word":"Four","imageUrl":""},
            "5":{"word":"Five","imageUrl":""},
            "6":{"word":"Six","imageUrl":""},
            "7":{"word":"Seven","imageUrl":""},
            "8":{"word":"Eight","imageUrl":""},
            "9":{"word":"Nine","imageUrl":""},
            "10":{"word":"Ten","imageUrl":""}
        }
                
        if len(fwUsers) > 0:
            fwUser = fwUsers[0]
            fwLesson = FWLesson(parent=fwUser.key,lessonName=lessonName, 
                                lessonContents = lessonContents, shared=True,
                                lessonDescription = lessonDescription,active=True,
                                lessonType=LESSON_TYPE_NUMBERS
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
            
        
