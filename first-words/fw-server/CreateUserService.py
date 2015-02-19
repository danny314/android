import webapp2

from FWUtils import getJsonObjFromFormData
from FWUser import FWUser

class CreateUserService(webapp2.RequestHandler):

    def post(self):
        print 'Create User Service post called'
        jsonObj = getJsonObjFromFormData(self.request.body)
        username = jsonObj['username']
        password = jsonObj['password']
        password2 = jsonObj['password2']
        
        savedUsers = FWUser.query(FWUser.username==username).fetch()
        
        if len(savedUsers) > 0:
            print 'User ' + username + ' already exists!'
            self.response.set_status(400)
            jsonResponse = '{"status":"FAILED","error":"USER_ALREADY_EXISTS"}'
        else:
            if password != password2:
                self.response.set_status(400)
                print 'Passwords do not match'
                jsonResponse = '{"status":"FAILED","error":"PASSWORD_NO_MATCH"}'
            else:    
                approvedLessons = [] 
                fwUser = FWUser(username=username, password=password, approvedLessons=approvedLessons)
                fwUserKey = fwUser.put()
                
                print 'User ' + username + ' created with key ' + str(fwUserKey)
                jsonResponse = '{"status":"OK"}'
                self.response.set_status(201)
        
        self.response.headers['Content-Type'] = 'application/json'
        self.response.out.write(jsonResponse)
            
        