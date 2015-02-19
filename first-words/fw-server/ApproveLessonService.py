import webapp2

from FWUser import FWUser
from FWLesson import FWLesson
from FWUtils import *

class ApproveLessonService(webapp2.RequestHandler):

    def post(self):
        print 'Approve Lesson Service post called'
        jsonObj = getJsonObjFromFormData(self.request.body)
        
        username = jsonObj['username']
        print 'received username = ' + username

        lessonName = jsonObj['lessonName']
        print 'received lesson name = ' + lessonName
        

        fwUsers = FWUser.query(FWUser.username == username).fetch()
        if len(fwUsers) > 0:
            fwUser = fwUsers[0]
            if len(lessonName) > 0:
                if (lessonName not in fwUser.approvedLessons):
                    print "Lesson " + lessonName + " not in approved list for user " + username + ". Approving..."
                    fwUser.approvedLessons.insert(0,lessonName)
                    fwUser.put()
                else:
                    print "Lesson " + lessonName + " already approved for user " + username     
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
            
        