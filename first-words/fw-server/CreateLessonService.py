import webapp2

from FWUtils import *
from FWUser import FWUser
from FWLesson import FWLesson

from google.appengine.api import search

class CreateLessonService(webapp2.RequestHandler):

    def post(self):
        print 'Create Lesson Service post called'
        jsonObj = getJsonObjFromFormData(self.request.body)
        username = jsonObj['username']
        
        lesson = json.loads(jsonObj['lesson'])
        lessonName = lesson['lessonName']
        
        print 'Type of lesson is ' + str(type(lesson))
        print 'Value lesson is ' + str(lesson)
        lessonContents = lesson['lessonContents']
        
        if lesson['shared'] == 'true':
            lessonShared = True
        else:
            lessonShared = False
            
        existingLessons = FWLesson.query(FWLesson.lessonName==lessonName).fetch()
        
        if len(existingLessons) > 0:
            self.response.set_status(400)
            print 'Lesson ' + lessonName + ' already exists!'
            jsonResponse = '{"status":"FAILED","error":"LESSON_ALREADY_EXISTS"}'
        else:
            fwUsers = FWUser.query(FWUser.username==username).fetch()
            
            if len(fwUsers) > 0:
                fwUser = fwUsers[0]
                if len(lesson) > 0:
                    print 'Creating new lesson ' + str(lesson) + ' for ' + fwUser.username
                    
                    fwLesson = FWLesson(parent=fwUser.key,lessonName=lessonName, 
                                        lessonContents = lessonContents, shared=lessonShared,
                                        lessonDescription = lesson['lessonDescription'],createdDate = getEpochTime()
                                        );
                    fwLessonKey = fwLesson.put()
                    
                    if (lessonName not in fwUser.approvedLessons):
                        print "Lesson " + lessonName + " not in approved list for user " + username + ". Approving..."
                        fwUser.approvedLessons.insert(0,lessonName)
                        fwUser.put()
                    else:
                        print "Lesson " + lessonName + " already approved for user " + username     

                    print 'Lesson saved with key ' + str(fwLessonKey)
                    
                    jsonResponse = '{"status":"OK"}'
                    self.response.set_status(201)
                    
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
            
        
