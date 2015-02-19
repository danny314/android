import webapp2

from FWUser import FWUser
from FWLesson import FWLesson
from FWUtils import *

class UnapproveLessonService(webapp2.RequestHandler):

    def post(self):
        print 'Unapprove Lesson Service post called'
        jsonObj = getJsonObjFromFormData(self.request.body)
        
        username = jsonObj['username']
        print 'received username = ' + username

        lessonName = jsonObj['lessonName']
        print 'received lesson name = ' + lessonName
        

        fwUsers = FWUser.query(FWUser.username == username).fetch()
        if len(fwUsers) > 0:
            fwUser = fwUsers[0]
            if len(lessonName) > 0:
                if (lessonName in fwUser.approvedLessons):
                    print "Lesson " + lessonName + " exists in approved lessons list for user " + username
                    fwUser.approvedLessons.remove(lessonName)
                    fwUser.put()
                else:
                    print "Lesson " + lessonName + " does not exist in approved lessons list for user " + username
                         
                jsonResponse = '{"status":"OK"}'
                self.response.set_status(200)
            else:
                self.response.set_status(400)
                print 'No lesson received!'
                jsonResponse = '{"status":"FAILED","error":"NO_LESSON_NAME"}'
        else:
            self.response.set_status(400)
            print 'Invalid user!'
            jsonResponse = '{"status":"FAILED","error":"INVALID_USER"}'
            
                
        
        self.response.headers['Content-Type'] = 'application/json'
        self.response.out.write(jsonResponse)
            
        