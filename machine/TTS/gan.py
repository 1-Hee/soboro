import torch.nn as nn
from torch.nn.utils import weight_norm

class residual_stack(nn.Module):
    def __init__(self, size, dilation):
        super().__init__()

        self.block = nn.Sequential(
            nn.LeakyReLU(0.2),
            nn.ReflectionPad1d(dilation),
            weight_norm(nn.Conv1d(size, size, kernel_size=3, dilation=dilation)),
            nn.LeakyReLU(0.2),
            weight_norm(nn.Conv1d(size, size, kernel_size=1))
        )
        self.shortcut = weight_norm(nn.Conv1d(size, size, kernel_size=1))

    def forward(self, x):
        return self.block(x) + self.shortcut(x)


def encoder_sequential(input_size, output_size, *args, **kwargs):
    return nn.Sequential(
        nn.LeakyReLU(0.2),
        weight_norm((nn.ConvTranspose1d(input_size, output_size, *args, **kwargs)))
    )


class Generator(nn.Module):
    def __init__(self, mel_dim):
        super().__init__()

        factor = [8, 8, 2, 2]

        layers = [
            nn.ReflectionPad1d(3), # 3+80+3 = 86
            weight_norm(nn.Conv1d(mel_dim, 512, kernel_size=7)),
        ]

        input_size = 512
        for f in factor:
            layers += [encoder_sequential(input_size,
                                          input_size // 2,
                                          kernel_size=f * 2,
                                          stride=f,
                                          padding=f // 2 + f % 2)]
            input_size //= 2
            for d in range(3):
                layers += [residual_stack(input_size, 3 ** d)]

        layers += [
            nn.LeakyReLU(0.2),
            nn.ReflectionPad1d(3),
            weight_norm(nn.Conv1d(32, 1, kernel_size=7)),
            nn.Tanh(),
        ]

        self.generator = nn.Sequential(*layers)

    def forward(self, x):
        return self.generator(x)