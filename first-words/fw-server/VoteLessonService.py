import webapp2

from FWUtils import *
from FWUser import FWUser
from FWLesson import FWLesson

class VoteLessonService(webapp2.RequestHandler):

    def post(self):
        print 'Vote Lesson Service post called'
        jsonObj = getJsonObjFromFormData(self.request.body)
        
        lessonName = jsonObj['lessonName']
        print 'received lesson name = ' + lessonName
        
        username = jsonObj['username']
        print 'received username = ' + username

        voteType = jsonObj['voteType']
        print 'received voteType = ' + voteType

        fwUsers = FWUser.query(FWUser.username == username).fetch()
        
        if len(fwUsers) > 0:
            fwUser = fwUsers[0]
            if len(lessonName) > 0:
                fwLessons = FWLesson.query(FWLesson.lessonName == lessonName).fetch()
                fwLesson = fwLessons[0]
                
                newVotes = fwLesson.votes
                
                if voteType == 'VOTE_UP':
                    newVotes = newVotes + 1
                elif voteType == 'VOTE_DOWN':
                    newVotes = newVotes - 1
                else:
                    print 'ERROR: unexpected voteType' + voteType + ' for lesson ' + lessonName
                         
                fwLesson.votes = newVotes
                print 'saved votes = ' + str(fwLesson.votes)
                fwLesson.put()
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
            
        