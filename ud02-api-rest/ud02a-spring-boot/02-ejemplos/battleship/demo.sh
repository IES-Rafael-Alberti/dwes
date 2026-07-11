#!/usr/bin/env bash
set -euo pipefail

BASE="http://localhost:8081/api"
GAME_ID=1

echo "========================================="
echo " Battleship API — Demo Script"
echo "========================================="
echo ""

# ── Games ────────────────────────────────────

echo "=== 1. Create game ==="
curl -s -X POST "$BASE/games" \
  -H "Content-Type: application/json" \
  -d '{"boardSize":10}' | python3 -m json.tool
echo ""

echo "=== 1b. List games ==="
curl -s "$BASE/games" | python3 -m json.tool
echo ""

echo "=== 1c. Get game $GAME_ID ==="
curl -s "$BASE/games/$GAME_ID" | python3 -m json.tool
echo ""

# ── Ships ────────────────────────────────────

echo "=== 2a. Place ship (vertical: Submarino, 3 cells) ==="
curl -s -X POST "$BASE/games/$GAME_ID/ships" \
  -H "Content-Type: application/json" \
  -d '{"shipName":"Submarino","length":3,"startX":2,"startY":3,"isHorizontal":false}' | python3 -m json.tool
echo ""

echo "=== 2b. Place ship (horizontal: Crucero, 4 cells) ==="
curl -s -X POST "$BASE/games/$GAME_ID/ships" \
  -H "Content-Type: application/json" \
  -d '{"shipName":"Crucero","length":4,"startX":5,"startY":1,"isHorizontal":true}' | python3 -m json.tool
echo ""

# ── Attacks ──────────────────────────────────

echo "=== 3a. Attack (miss at 0,0) ==="
curl -s -X POST "$BASE/games/$GAME_ID/attacks" \
  -H "Content-Type: application/json" \
  -d '{"x":0,"y":0}' | python3 -m json.tool
echo ""

echo "=== 3b. Attack (hit Submarino at 2,3) ==="
curl -s -X POST "$BASE/games/$GAME_ID/attacks" \
  -H "Content-Type: application/json" \
  -d '{"x":2,"y":3}' | python3 -m json.tool
echo ""

echo "=== 3c. Attack (hit Submarino at 2,4) ==="
curl -s -X POST "$BASE/games/$GAME_ID/attacks" \
  -H "Content-Type: application/json" \
  -d '{"x":2,"y":4}' | python3 -m json.tool
echo ""

echo "=== 3d. Attack (sink Submarino at 2,5) ==="
curl -s -X POST "$BASE/games/$GAME_ID/attacks" \
  -H "Content-Type: application/json" \
  -d '{"x":2,"y":5}' | python3 -m json.tool
echo ""

# ── Validation errors ────────────────────────

echo "=== 4a. Error: out of bounds ==="
curl -s -X POST "$BASE/games/$GAME_ID/attacks" \
  -H "Content-Type: application/json" \
  -d '{"x":-1,"y":0}' | python3 -m json.tool
echo ""

echo "=== 4b. Error: repeat attack ==="
curl -s -X POST "$BASE/games/$GAME_ID/attacks" \
  -H "Content-Type: application/json" \
  -d '{"x":0,"y":0}' | python3 -m json.tool
echo ""

echo "=== 4c. Error: duplicate ship name ==="
curl -s -X POST "$BASE/games/$GAME_ID/ships" \
  -H "Content-Type: application/json" \
  -d '{"shipName":"Submarino","length":2,"startX":7,"startY":7,"isHorizontal":true}' | python3 -m json.tool
echo ""

# ── Final state ──────────────────────────────

echo "=== 5. Final game state ==="
curl -s "$BASE/games/$GAME_ID" | python3 -m json.tool
echo ""

echo "========================================="
echo " Done!"
echo "========================================="
