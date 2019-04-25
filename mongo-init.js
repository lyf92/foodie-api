var res = [
  "use foodie;",
  db.createUser({
    user: 'foodie',
    pwd: 'foodie',
    roles: [{
      role: "readWrite", db: "foodie"
    }]
  })
]
printjson(res)