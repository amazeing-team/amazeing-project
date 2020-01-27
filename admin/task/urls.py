from django.conf.urls import url, include
from rest_framework import routers
from .views import *

from rest_framework.documentation import include_docs_urls
from rest_framework.schemas import get_schema_view

router = routers.DefaultRouter()
router.register(r'tasks', TaskViewSet)

schema_view = get_schema_view(title="Vorkurs")

urlpatterns = [
    url(r'^api/', include(router.urls)),
    url(r'^schema/', schema_view),
    url(r'^docs/', include_docs_urls(title='Vorkurs')),
]
