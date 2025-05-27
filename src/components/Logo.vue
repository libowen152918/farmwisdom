<template>
  <div class="logo-container">
    <div class="logo-background">
      <div class="wave-container">
        <div class="wave"></div>
        <div class="wave"></div>
        <div class="wave"></div>
      </div>
      <div class="dots-grid"></div>
    </div>
    <h1 class="logo-text">
      <span class="bird-icon">🕊</span>
      天作美论坛
    </h1>
  </div>
</template>

<style scoped>
.logo-container {
  position: relative;
  width: 100%;
  height: 200px;
  overflow: hidden;
  background: linear-gradient(135deg, #2E7D32 0%, #4CAF50 100%);
}

.logo-background {
  position: absolute;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.wave-container {
  position: absolute;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.wave {
  position: absolute;
  width: 200%;
  height: 200%;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 45%;
  animation: wave 20s infinite linear;
}

.wave:nth-child(1) {
  animation-duration: 20s;
  opacity: 0.3;
}

.wave:nth-child(2) {
  animation-duration: 15s;
  opacity: 0.2;
  animation-delay: -5s;
}

.wave:nth-child(3) {
  animation-duration: 10s;
  opacity: 0.1;
  animation-delay: -2s;
}

.dots-grid {
  position: absolute;
  width: 100%;
  height: 100%;
  background-image: radial-gradient(rgba(255, 255, 255, 0.2) 1px, transparent 1px);
  background-size: 20px 20px;
  animation: moveGrid 40s infinite linear;
}

.logo-text {
  position: relative;
  text-align: center;
  color: #FFD700;
  font-size: 3rem;
  font-weight: bold;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
  margin: 0;
  padding-top: 70px;
  animation: textGlow 2s infinite alternate;
  font-family: "STXingkai", "华文行楷", serif;
  letter-spacing: 4px;
}

.bird-icon {
  font-size: 2rem;
  margin-right: 10px;
  display: inline-block;
  animation: flyBird 2s infinite ease-in-out;
}

@keyframes wave {
  0% {
    transform: translate(-50%, -50%) rotate(0deg);
  }
  100% {
    transform: translate(-50%, -50%) rotate(360deg);
  }
}

@keyframes moveGrid {
  0% {
    background-position: 0 0;
  }
  100% {
    background-position: 40px 40px;
  }
}

@keyframes textGlow {
  from {
    text-shadow: 0 0 10px rgba(255, 215, 0, 0.5),
                 0 0 20px rgba(255, 215, 0, 0.3),
                 0 0 30px rgba(255, 215, 0, 0.2);
  }
  to {
    text-shadow: 0 0 20px rgba(255, 215, 0, 0.8),
                 0 0 30px rgba(255, 215, 0, 0.5),
                 0 0 40px rgba(255, 215, 0, 0.3);
  }
}

@keyframes flyBird {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-5px) rotate(5deg);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .logo-container {
    height: 150px;
  }
  
  .logo-text {
    font-size: 2rem;
    padding-top: 50px;
  }
  
  .bird-icon {
    font-size: 1.5rem;
  }
}
</style> 