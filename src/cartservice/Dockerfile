FROM mcr.microsoft.com/dotnet/sdk:9.0 AS build
WORKDIR /app
COPY src/*.csproj .
COPY tests/*.csproj tests/
RUN dotnet restore
COPY . .

# Run tests first
RUN dotnet test tests/cartservice.tests.csproj --verbosity normal

# Build the application
RUN dotnet publish src/cartservice.csproj -c Release -o out

FROM mcr.microsoft.com/dotnet/aspnet:9.0
WORKDIR /app
COPY --from=build /app/out .

ENV PORT=7006
ENV ASPNETCORE_URLS=http://*:7006
ENV REDIS_ADDR=redis:6379

EXPOSE 7006
ENTRYPOINT ["dotnet", "cartservice.dll"]