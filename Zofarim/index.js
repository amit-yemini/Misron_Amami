const express = require("express");
const app = express();
const PORT = 3000;

app.use(express.json());

app.post("/services/alert", (req, res) => {
  console.log("got alert ", req.body);
  res.status(200).send("zofarimming");
});

app.listen(PORT, () => {
  console.log(`Server is running on http://localhost:${PORT}`);
});
