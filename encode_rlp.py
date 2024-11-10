import rlp
hashes = [
    "fc2b3b6db38a51db3b9cb95de29b719de8deb99630626e4b4b99df056ffb7f2e",
    "48603f7adff7fbfc2a10b22a6710331ee68f2e4d1cd73a584d57c8821df79356",
    "cbc920e7bb89cbcb540a469a16226bf1057825283ab8eac3f45d00811eef8a64",
    "6d604ffc644a282fca8cb8e778e1e3f8245d8bd1d49326e3016a3c878ba0cbbd"
]
encoded = [bytes.fromhex(h) for h in hashes]
rlp = rlp.encode(encoded)
print(rlp.hex())