import random


def generate_letter_matrix() -> list:
    alphabet = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'

    common_letters = random.sample(alphabet, 2)

    remaining = [l for l in alphabet if l not in common_letters]

    list_one_unique = random.sample(remaining, 4)

    remaining_for_two = [l for l in remaining if l not in list_one_unique]
    list_two_unique = random.sample(remaining_for_two, 4)

    list_one = common_letters + list_one_unique
    list_two = common_letters + list_two_unique

    random.shuffle(list_one)
    random.shuffle(list_two)

    return [list_one, list_two, common_letters]