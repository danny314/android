import webapp2

from CreateUserService import CreateUserService
from LoginUserService import LoginUserService
from CreateLessonService import CreateLessonService
from GetLessonsService import GetLessonsService
from DeleteLessonService import DeleteLessonService
from UpdateLessonService import UpdateLessonService
from UpdateLastLessonService import UpdateLastLessonService
from GetSingleLessonService import GetSingleLessonService
from GetLastLessonService import GetLastLessonService
from ActivateLessonService import ActivateLessonService
from CreateNumbersLessonService import CreateNumbersLessonService
from CreateColorsLessonService import CreateColorsLessonService
from ApproveLessonService import ApproveLessonService
from UnapproveLessonService import UnapproveLessonService
from GetApprovedLessonsService import GetApprovedLessonsService
from GetUserLessonsService import GetUserLessonsService
from GetOwnLessonsService import GetOwnLessonsService
from SearchLessonsService import SearchLessonsService
from VoteLessonService import VoteLessonService

application = webapp2.WSGIApplication([
    ('/api/user/create', CreateUserService),
    ('/api/user/authenticate', LoginUserService),
    ('/api/user/update/lastlesson', UpdateLastLessonService),
    ('/api/user/lesson/get', GetUserLessonsService),
    ('/api/user/lesson/getown', GetOwnLessonsService),
    ('/api/user/approve/lesson', ApproveLessonService),
    ('/api/user/unapprove/lesson', UnapproveLessonService),
    ('/api/user/lesson/getapproved', GetApprovedLessonsService),
    ('/api/lesson/create', CreateLessonService),
    ('/api/admin/lesson/create/numbers', CreateNumbersLessonService),
    ('/api/admin/lesson/create/colors', CreateColorsLessonService),
    ('/api/lesson/delete', DeleteLessonService),
    ('/api/lesson/search', SearchLessonsService),
    ('/api/lesson/update', UpdateLessonService),
    ('/api/lesson/activate', ActivateLessonService),
    ('/api/lesson/vote', VoteLessonService),
    ('/api/lesson/get', GetLessonsService),
    ('/api/lesson/getsingle', GetSingleLessonService),
    ('/api/lesson/getlast', GetLastLessonService)
], debug=True)
