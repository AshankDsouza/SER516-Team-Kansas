from flask import Flask, render_template, request, redirect, url_for, session, jsonify
import os
from taigaApi.project.getProjectMilestones import (
    get_number_of_milestones,
    get_milestone_id,
)
from taigaApi.customAttributes.getCustomAttributes import (
    get_business_value_data_for_sprint,
    get_business_value_id,
    get_user_story_business_value_map,
    get_custom_attribute_values,
)
import requests

app = Flask(__name__)

@app.route("/value-auc-data", methods=["POST"])
def value_auc_data():
    data = request.get_json()
    session = data["session"]
    sprintMapping, sprints = get_number_of_milestones(
        session["project_id"], session["auth_token"]
    )
    auc_map = dict()
    for sprint_id in list(sprintMapping.values()):
        running_bv_data, ideal_bv_data = get_business_value_data_for_sprint(
            session["project_id"], sprint_id, session["auth_token"]
        )
        total_bv_for_sprint = list(ideal_bv_data.values())[0]
        if total_bv_for_sprint:
            bv_auc_delta = (
                lambda: {
                    item: round(
                        abs(
                            (total_bv_for_sprint - running_bv_data[item])
                            / total_bv_for_sprint
                            - (total_bv_for_sprint - ideal_bv_data[item])
                            / total_bv_for_sprint
                        ),
                        2,
                    )
                    for item in running_bv_data.keys()
                }
            )()
            auc_map[sprint_id] = sum(list(bv_auc_delta.values()))
        else:
            auc_map[sprint_id] = 0
    auc = dict()
    for item in sprintMapping.items():
        auc["Sprint " + str(sprints - int(item[0]) + 1)] = auc_map[item[1]] * 100
    auc_list = list(auc.items())
    auc_list.sort(key=lambda x: x[0])
    result = []
    for item in auc_list:
        result.append(
            {
                "Sprint":item[0],
                "Value":item[1]
            }
        )
    return result
