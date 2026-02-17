from django.urls import path
from main import views

app_name = 'main'
urlpatterns = [
    path('', views.index, name='index'),
    path('session/<uuid:session_id>/', views.session_view, name='session'),
    path('session/<uuid:session_id>/select/', views.select_letter, name='select_letter'),
    path('session/<uuid:session_id>/next/', views.next_round, name='next_round'),
    # path('mobile/<uuid:session_id>/', views.mobile_session_view, name='mobile_session'),
    path('statistics/<uuid:session_id>/', views.get_statistics, name='statistics'),
    path('session/<uuid:session_id>/download/csv/', views.download_csv, name='download_csv'),

    # # API endpoints
    path('api/start/', views.start_session, name='start_session'),
    path('api/round/<uuid:session_id>/', views.api_get_round, name='api_get_round'),
    path('api/session/<uuid:session_id>/stop/', views.api_stop_session, name='api_stop_session'),

    # # QR code generation
    path('api/session/<uuid:session_id>/qr/', views.generate_qr, name='qr_code'),
]