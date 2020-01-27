from django.shortcuts import render

from .models import Task
from rest_framework import viewsets
from .serializers import TaskSerializer


class TaskViewSet(viewsets.ReadOnlyModelViewSet):
    """
    API endpoint that allows task to be viewed and edited
    """
    queryset = Task.objects.all()
    serializer_class = TaskSerializer
