

HABIT_SCORE = 140
GOAL_SCORE = 500
CHEKPOINT_SCORE = 250

def habit_rating(intensity, votes, reputation):
    if reputation == 0 and votes == 0:
        coef = 1
    elif reputation < 0:
        coef = 1 + reputation / votes
    elif reputation



    return HABIT_SCORE


def goal_rating():
    pass


def checkpoint_rating():
    pass