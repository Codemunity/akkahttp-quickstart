# For more information: https://devcenter.heroku.com/articles/container-registry-and-runtime

APP_NAME=$1
TAG=$2

echo "Deploying $TAG to $APP_NAME"

# Login to Docker registry
docker login --username=_ --password-stdin registry.heroku.com

# Create local Docker image
sbt docker:publishLocal

# Create Heroku tag
docker tag akkahttp-quickstart:$TAG registry.heroku.com/$APP_NAME/web

# Push Heroku tag
docker push registry.heroku.com/$APP_NAME/web

# Release
curl -n -X PATCH https://api.heroku.com/apps/$APP_NAME/formation \
  -d '{
  "updates": [
    {
      "type": "web",
      "docker_image": "'"$(docker inspect registry.heroku.com/$APP_NAME/web --format={{.Id}})"'"
    }
  ]
}' \
  -H "Content-Type: application/json" \
  -H "Accept: application/vnd.heroku+json; version=3.docker-releases"

