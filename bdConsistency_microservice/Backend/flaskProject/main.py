from flask import Flask, render_template, jsonify
import json
from datetime import datetime, timedelta

from taigaApi.milestones.getMilestonesForSprint import get_milestones_by_sprint

from taigaApi.customAttributes.getCustomAttributes import get_business_value_data_for_sprint


app = Flask(__name__)

@app.route("/<project_id>/<sprint_id>/<auth_token>/bd-calculation", methods=["GET"])
def calclateBDConsistency(project_id, sprint_id, auth_token):
    try:
        # Data required to plot is stored here
        data_to_plot = { 
                "total_story_points": 0,
                "x_axis": [],
                "bv_projection": [],
                "story_points_projection": []
        }

        milestone = get_milestones_by_sprint(project_id, sprint_id, auth_token)

        for user_story in milestone["user_stories"]:
            if user_story["total_points"] == None: 
                continue

            data_to_plot["total_story_points"] += int(user_story["total_points"])
                
        start_date  = datetime.strptime(milestone["estimated_start"], '%Y-%m-%d')
        end_date = datetime.strptime(milestone["estimated_finish"], '%Y-%m-%d')

        data_to_plot["x_axis"] = [(start_date + timedelta(days=day)).strftime("%d %b %Y") for day in range((end_date - start_date).days + 1)]

        for index in range(0, len(data_to_plot["x_axis"])):
            current_processing_date = data_to_plot["x_axis"][index]
            current_processing_date_points = 0

            if index <= 0:
                current_processing_date_points = data_to_plot["total_story_points"]
            else:
                current_processing_date_points = data_to_plot["story_points_projection"][index - 1]

            total_points_completed = 0
            for user_story in milestone["user_stories"]:
                if(user_story["finish_date"] == None or user_story["total_points"] == None):
                    continue

                finish_date = datetime.fromisoformat(user_story["finish_date"]).strftime("%d %b %Y")
                        
                if(finish_date != current_processing_date):
                    continue

                total_points_completed = user_story["total_points"] + total_points_completed

            data_to_plot["story_points_projection"].append(round(current_processing_date_points - total_points_completed, 1))

        running_bv_data, _ = get_business_value_data_for_sprint(project_id, sprint_id, auth_token)
        data_to_plot['bv_projection'] = running_bv_data

        return jsonify({ "data_to_plot": data_to_plot }), 200
    except Exception as e:
        # Handle errors during the API request and print an error message
        print(e)
        return json.dumps({ "msg": "Failed plot bd consistency data" }), 500
    