from django.contrib import admin

from main.models import Session, Round, Statistics


@admin.register(Session)
class SessionAdmin(admin.ModelAdmin):
    list_display = ['session_id', 'created_at', 'current_round', 'is_active']
    list_filter = ['is_active', 'created_at']
    search_fields = ['session_id']


@admin.register(Round)
class RoundAdmin(admin.ModelAdmin):
    list_display = ['session', 'round_number', 'is_completed', 'is_correct', 'response_time']
    list_filter = ['is_completed', 'is_correct']
    search_fields = ['session__session_id']


@admin.register(Statistics)
class StatisticsAdmin(admin.ModelAdmin):
    list_display = ['session', 'total_time', 'average_time', 'correct_count', 'mistakes_count']
