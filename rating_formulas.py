import math

HABIT_SCORE = 140
GOAL_SCORE = 500
CHEKPOINT_SCORE = 250


def sigmoid(x):
    return 1 / (1 + math.exp(-x))


def habit_rating(intensity, votes, reputation):
    if votes == 0:
        return HABIT_SCORE / 2
    elif reputation > 0:
        x = reputation / votes
    elif reputation < 0:
        x = reputation / votes
    else:
        x = reputation / votes
    return int(sigmoid(x) * HABIT_SCORE / intensity)


def goal_rating():
    pass


def checkpoint_rating():
    pass
