import webapp2

from FWUser import FWUser
from FWLesson import FWLesson
from FWUtils import *

class UpdateLastLessonService(webapp2.RequestHandler):

    def post(self):
        print 'Update Last Lesson Service post called'
        jsonObj = getJsonObjFromFormData(self.request.body)
        
        username = jsonObj['username']

        lessonName = jsonObj['lessonName']
        print 'received lesson name = ' + lessonName
        
        print 'received username = ' + username

        fwUsers = FWUser.query(FWUser.username == username).fetch()
        
        if len(fwUsers) > 0:
            fwUser = fwUsers[0]
            if len(lessonName) > 0:
                fwUser.lastLesson = lessonName
                fwUser.put()
                jsonResponse = '{"status":"OK"}'
                self.response.set_status(200)
            else:
                self.response.set_status(400)
                print 'No lesson received!'
                jsonResponse = '{"status":"FAILED","error":"NO_LESSON_DATA"}'
        else:
            self.response.set_status(400)
            print 'Invalid user!'
            jsonResponse = '{"status":"FAILED","error":"INVALID_USER"}'
            
                
        
        self.response.headers['Content-Type'] = 'application/json'
        self.response.out.write(jsonResponse)
            
        