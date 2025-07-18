from fastapi import FastAPI
from pydantic import BaseModel
from kiwipiepy import Kiwi

app = FastAPI()
kiwi = Kiwi()

class TextRequest(BaseModel):
    text: str

@app.post("/spacing")
async def spacing(req: TextRequest):
    return {"result": kiwi.space(req.text)}