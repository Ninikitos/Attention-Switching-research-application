from django.urls import re_path
from main import consumers

websocket_urlpatterns = [
    re_path(r'^ws/session/(?P<session_id>[^/]+)/?$', consumers.RoundConsumer.as_asgi()),
]